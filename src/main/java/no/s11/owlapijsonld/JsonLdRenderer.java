package no.s11.owlapijsonld;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

public class JsonLdRenderer extends RDFRendererAdapter {
	
	private Writer writer;
	private StringWriter tripleWriter;

	public JsonLdRenderer(OWLOntology ontology, Writer writer,
			OWLOntologyFormat format) {
		super(ontology, format);
		this.writer = writer;
		this.tripleWriter = new StringWriter();
	}

	@Override
	protected void endDocument() throws IOException {
		String triples = tripleWriter.toString();
		// Poor-man's attempt to generate N-Quads compatible format
		triples = triples.replace(" -> ", " "); 
		triples = triples.replace("\n", " .\n");
		
//		System.out.println("Triples: ");
//		System.out.println(triples);
		JsonLdOptions options = new JsonLdOptions();
		
		try {
			Object json = JsonLdProcessor.fromRDF(triples, options);
			URL owlJsonLD = getClass().getResource("owl.jsonld");
			Object context = JsonUtils.fromURL(owlJsonLD);
			json = JsonLdProcessor.flatten(json, context, options);
			
			JsonUtils.writePrettyPrint(writer, json);			
		} catch (JsonLdError e) {
			throw new IOException(e);
		}		
	}

	@Override
	public void render(RDFResourceNode node) throws IOException {
		getGraph().dumpTriples(tripleWriter);
	}

}
