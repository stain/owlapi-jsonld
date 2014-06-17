package no.s11.owlapijsonld;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.coode.owlapi.rdf.renderer.RDFRendererBase;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

public class JsonLdRenderer extends RDFRendererBase {

	
	private Writer writer;

	public JsonLdRenderer(OWLOntology ontology, Writer writer,
			OWLOntologyFormat format) {
		super(ontology, format);
		this.writer = writer;
	}

	@Override
	protected void beginDocument() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void endDocument() throws IOException {
		Writer tmpWriter = new StringWriter();
		getGraph().dumpTriples(tmpWriter);
		JsonLdOptions options = new JsonLdOptions();
		options.outputForm = "compact";		
		try {
			Object json = JsonLdProcessor.fromRDF(tmpWriter.toString(), options);
			
			JsonUtils.writePrettyPrint(writer, json);			
		} catch (JsonLdError e) {
			throw new IOException(e);
		}

		
	}

	@Override
	protected void writeAnnotationPropertyComment(OWLAnnotationProperty prop)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeDataPropertyComment(OWLDataProperty prop)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeObjectPropertyComment(OWLObjectProperty prop)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeClassComment(OWLClass cls) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeDatatypeComment(OWLDatatype datatype)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeIndividualComments(OWLNamedIndividual ind)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeBanner(String name) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(RDFResourceNode node) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
