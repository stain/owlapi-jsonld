package no.s11.owlapijsonld;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.coode.owlapi.rdfxml.parser.AnonymousNodeChecker;
import org.semanticweb.owlapi.io.AbstractOWLParser;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.UnloadableImportException;

import uk.ac.manchester.cs.owl.owlapi.turtle.parser.OWLRDFConsumerAdapter;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;
import com.github.jsonldjava.utils.JsonUtils;

public class JSONLDParser extends AbstractOWLParser implements OWLParser {

	protected OWLDataFactory owlDataFactory;	

	public JSONLDParser(OWLDataFactory owlDataFactory) {
		this.owlDataFactory = owlDataFactory;
	}
	
	private class OWLTripleCallback implements JsonLdTripleCallback {
		private final OWLRDFConsumerAdapter consumer;

		private OWLTripleCallback(OWLRDFConsumerAdapter consumer) {
			this.consumer = consumer;
		}

		public Object call(RDFDataset dataset) {
		    for (String graphName : dataset.graphNames()) {
		        final java.util.List<RDFDataset.Quad> quads = dataset.getQuads(graphName);
		        if ("@default".equals(graphName)) {
		            graphName = null;
		        }
		        for (final RDFDataset.Quad quad : quads) {
		            if (quad.getObject().isLiteral()) {
		                triple(quad.getSubject(), quad.getPredicate(), quad.getObject().getValue(),
		                        quad.getObject().getDatatype(), quad.getObject().getLanguage(),
		                        graphName);
		            } else {
		                triple(quad.getSubject(), quad.getPredicate(), quad.getObject(), graphName);			                    			                    
		            }
		        }
		    }
		    return consumer.getOntology();
		}
		

		private void triple(Node subject, Node predicate, String value,
				String datatype, String language, String graphName) {
			OWLLiteral literal = asOwlLiteral(value, datatype, language);
			consumer.addTriple(asIri(subject), asIri(predicate), literal);					
		}

		private OWLLiteral asOwlLiteral(String value, String datatype,
				String language) {
			if (language != null) { 
				return owlDataFactory.getOWLLiteral(value, language);
			} else if (datatype == null) { 
				return owlDataFactory.getOWLLiteral(value);
			} else { 
				OWLDatatype owltype = owlDataFactory.getOWLDatatype(IRI.create(datatype));						
				return owlDataFactory.getOWLLiteral(value, owltype);
			}					
		}

		private void triple(Node subject, Node predicate, Node object,
				String graphName) {
			// Ignore 'graphname' for now and load them all as a single ontology..
			consumer.addTriple(asIri(subject), asIri(predicate), asIri(object));
			
		}

		private IRI asIri(Node object) {
			if (object.isIRI()) { 
				return IRI.create(object.getValue());
			} else {
				// BNode
				if (! object.getValue().startsWith("_:")) {
					// uh...??
					return IRI.create(object.getValue(), null);
				} else { 
					String bnode = object.getValue().substring(2);
					return IRI.create("genid" + bnode, null);
				}
			}
		}
	}



	@Override
	public OWLOntologyFormat parse(OWLOntologyDocumentSource documentSource,
			OWLOntology ontology) throws OWLParserException,
			UnloadableImportException, IOException {
		return parse(documentSource, ontology,
				new OWLOntologyLoaderConfiguration());
	}

	@Override
	public OWLOntologyFormat parse(OWLOntologyDocumentSource documentSource,
			OWLOntology ontology, OWLOntologyLoaderConfiguration configuration)
			throws OWLParserException, IOException, OWLOntologyChangeException,
			UnloadableImportException {
		Reader reader = null;
		InputStream is = null;
		try {
			Object jsonObject;
			
			if (documentSource.isReaderAvailable()) {
				jsonObject = JsonUtils.fromReader(documentSource.getReader());
				
			} else if (documentSource.isInputStreamAvailable()) {
				jsonObject = JsonUtils.fromInputStream(documentSource.getInputStream());
			} else {
				jsonObject = JsonUtils.fromURL(documentSource.getDocumentIRI()
						.toURI().toURL());
			}
			
			
			final OWLRDFConsumerAdapter consumer = new OWLRDFConsumerAdapter(
					ontology, new AnonymousNodeChecker() {

						@Override
						public boolean isAnonymousNode(IRI iri) {
							return NodeID.isAnonymousNodeIRI(iri);
						}

						@Override
						public boolean isAnonymousSharedNode(String iri) {
							return NodeID.isAnonymousNodeID(iri);
						}

						@Override
						public boolean isAnonymousNode(String iri) {
							return NodeID.isAnonymousNodeIRI(iri);
						}
					}, configuration);
			
			
			JsonLdTripleCallback callback = new OWLTripleCallback(consumer);
			try {
				JsonLdProcessor.toRDF(jsonObject, callback);
			} catch (JsonLdError e) {
				throw new OWLParserException("Can't parse JSON-LD ontology " + documentSource , e);
			}
			JSONLDOntologyFormat format = new JSONLDOntologyFormat();
			consumer.setOntologyFormat(format);			
			//PrefixManager prefixManager = parser.getPrefixManager();
			
			// TODO: Set prefixes from JSON-LD top-level context...?
//			for (String prefixName : prefixManager.getPrefixNames()) {
//				format.setPrefix(prefixName,
//						prefixManager.getPrefix(prefixName));
//			}
			return format;		
		} finally {
			if (is != null) {
				is.close();
			} else if (reader != null) {
				reader.close();
			}
		}
	}

}
