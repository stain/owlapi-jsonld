package no.s11.owlapijsonld;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.coode.owlapi.rdf.model.RDFLiteralNode;
import org.coode.owlapi.rdf.model.RDFNode;
import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.coode.owlapi.rdf.model.RDFTriple;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

public class JsonLdRenderer extends RDFRendererAdapter {
	
	private Writer writer;
	List<Map> triplesJson = new ArrayList<>();
	
	public JsonLdRenderer(OWLOntology ontology, Writer writer,
			OWLOntologyFormat format) {
		super(ontology, format);
		this.writer = writer;		
	}

	@Override
	protected void endDocument() throws IOException {

		//System.out.println(triples);
		HashMap<String, Object> json = new HashMap<>();
		json.put("@graph", triplesJson);
		
		//System.out.println(JsonUtils.toPrettyString(json));
		
		try {
			URL owlJsonLD = getClass().getResource("owl.jsonld");
			Object context = JsonUtils.fromURL(owlJsonLD);
			JsonLdOptions options = new JsonLdOptions();
			Object flattened = JsonLdProcessor.flatten(json, context, options);			
			JsonUtils.writePrettyPrint(writer, flattened);			
		} catch (JsonLdError e) {
			throw new IOException(e);
		}		
	}

	private void triplesToJsonLd(Collection<RDFTriple> triples) {
		for (RDFTriple t : triples) {			
			Map<String, Object> jsonTriple = new HashMap<>();
			jsonTriple = objectToJson(t.getSubject());
			// TODO: Do we need to express bnodes differently?
			jsonTriple.put("@id", t.getSubject().getIRI().toString());
			jsonTriple.put(t.getProperty().getIRI().toString(), 
					objectToJson(t.getObject()));
			triplesJson.add(jsonTriple);
		}
	}

	private Map<String, Object> objectToJson(RDFNode object) {
		Map<String, Object> json = new HashMap<>();		
		if (object.isLiteral()) {
			RDFLiteralNode literal = (RDFLiteralNode)object;
			json.put("@value", literal.getLiteral());
			if (literal.getDatatype() != null) {
				json.put("@type", literal.getDatatype().toString());
			} else if (literal.getLang() != null) {
				json.put("@lang", literal.getLang());
			}			
		} else { 
			json.put("@id", object.getIRI().toString());
		}
		return json;
	}

	@Override
	public void render(RDFResourceNode node) throws IOException {
		// OWL-API RDFRendererBase does not give us access
		// to the triples in getGraph() !! We'll have to 
		// do a very slow recurse down every possible subject  :-(
				
		// Note: seen is initialized per node as each node get
		// its own getGraph().		
		HashSet<RDFResourceNode> seen = new HashSet<RDFResourceNode>();
		findAllStatements(node, seen);
		// Add anonymous nodes (which might themselves mention new
		// subjects)
		for (RDFResourceNode s: getGraph().getRootAnonymousNodes()) {
			findAllStatements(s, seen);
		}
		
	}

	/**
	 * Extract statements for nodes that are not seen.
	 * <p>
	 * Add node to <code>seen</code>,
	 * extract all its triples to <code>triples</code>, then 
	 * recurse down all possible objects (skipping
	 * those already seen).
	 * 
	 */
	private void findAllStatements(RDFResourceNode node, HashSet<RDFResourceNode> seen) {
		if (seen.contains(node)) {
			return;
		}
		seen.add(node);
		Collection<RDFTriple> newTriples = getGraph().getTriplesForSubject(node, false);
		triplesToJsonLd(newTriples);
		for (RDFTriple triple : newTriples) {
			RDFNode object = triple.getObject();
			if (object instanceof RDFResourceNode) {
				findAllStatements((RDFResourceNode) object, seen);
			}
		}
	}

}
