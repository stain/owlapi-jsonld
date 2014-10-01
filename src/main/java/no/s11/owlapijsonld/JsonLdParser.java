package no.s11.owlapijsonld;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.coode.owlapi.rdfxml.parser.AnonymousNodeChecker;
import org.coode.owlapi.rdfxml.parser.OWLRDFConsumer;
import org.semanticweb.owlapi.io.AbstractOWLParser;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;
import com.github.jsonldjava.utils.JsonUtils;

public class JsonLdParser extends AbstractOWLParser implements OWLParser {
	
	private static final String BNODE_PREFIX = "app://9bf9b875-d612-43d2-9d4b-3a50e3a5fd5b/";
	
	private class OWLTripleCallback implements JsonLdTripleCallback {
		
		private final OWLRDFConsumer consumer;
		
		private final Map<String, String> prefixes = new HashMap<>();
		
		private OWLTripleCallback(OWLRDFConsumer consumer) {
			this.consumer = consumer;
		}

		public Object call(RDFDataset dataset) {
			prefixes.putAll(dataset.getNamespaces());
			
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
		    try {
				consumer.endModel();
			} catch (SAXException e) {
				throw new OWLRuntimeException(e);
			}
		    return consumer.getOntology();
		}
		

		private void triple(Node subject, Node predicate, String value,
				String datatype, String language, String graphName) {
			//OWLLiteral literal = asOwlLiteral(value, datatype, language);
			try {
				consumer.statementWithLiteralValue(asIri(subject), asIri(predicate), value, language, datatype);
			} catch (SAXException e) {
				throw new OWLRuntimeException(e);
			}
		}

		private void triple(Node subject, Node predicate, Node object,
				String graphName) {
			// Ignore 'graphname' for now and load them all as a single ontology..
			try {
				consumer.statementWithResourceValue(asIri(subject), asIri(predicate), asIri(object));
			} catch (SAXException e) {
				throw new OWLRuntimeException(e);
			}
		}

		private String asIri(Node object) {
			if (object.isIRI()) { 
				return object.getValue();
			} else {
				String bnode = object.getValue().substring(2);
				return BNODE_PREFIX + bnode;
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
			try { 
				if (documentSource.isReaderAvailable()) {
					jsonObject = JsonUtils.fromReader(documentSource.getReader());
					
				} else if (documentSource.isInputStreamAvailable()) {
					jsonObject = JsonUtils.fromInputStream(documentSource.getInputStream());
				} else {
					jsonObject = JsonUtils.fromURL(documentSource.getDocumentIRI()
							.toURI().toURL());
				}
			} catch (JsonParseException e) {
				// Don't make it look like an IOException
				// as it blocks loading other formats
				throw new OWLParserException(e);
			}
			
			
			final OWLRDFConsumer consumer = new OWLRDFConsumer(
					ontology, new AnonymousNodeChecker() {
						@Override
						public boolean isAnonymousNode(IRI iri) {
							return isAnonymousNode(iri.toString());
						}
						@Override
						public boolean isAnonymousSharedNode(String iri) {
							return isAnonymousNode(iri);
						}
						@Override
						public boolean isAnonymousNode(String iri) {
							return iri.startsWith(BNODE_PREFIX);
						}
					}, configuration);
			
			
			OWLTripleCallback callback = new OWLTripleCallback(consumer);
			try {
				JsonLdProcessor.toRDF(jsonObject, callback);
			} catch (JsonLdError e) {
				throw new OWLParserException("Can't parse JSON-LD ontology " + documentSource , e);
			}
			JsonLdOntologyFormat format = new JsonLdOntologyFormat();
			consumer.setOntologyFormat(format);
			
			for(Entry<String, String> nextPrefix : callback.prefixes.entrySet()) {
				format.setPrefix(nextPrefix.getKey(), nextPrefix.getValue());
			}
			
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
