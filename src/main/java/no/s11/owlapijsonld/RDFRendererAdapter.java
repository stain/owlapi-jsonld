package no.s11.owlapijsonld;

import java.io.IOException;

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

/**
 * Adapter for RDFRendererBase
 * <p>
 * Extending  {@link RDFRendererBase} so that the only required
 * method to implement is {@link RDFRendererBase#render(RDFResourceNode)}
 * 
 * @author Stian Soiland-Reyes
 *
 */
public abstract class RDFRendererAdapter extends RDFRendererBase {

	protected RDFRendererAdapter(OWLOntology ontology) {
		super(ontology);
	}
	
	protected RDFRendererAdapter(OWLOntology ontology, OWLOntologyFormat format) {
		super(ontology, format);
	}

	@Override
	protected void beginDocument() throws IOException {
	}

	@Override
	protected void endDocument() throws IOException {
	}

	@Override
	protected void writeAnnotationPropertyComment(OWLAnnotationProperty prop)
			throws IOException {
	}

	@Override
	protected void writeDataPropertyComment(OWLDataProperty prop)
			throws IOException {
	}

	@Override
	protected void writeObjectPropertyComment(OWLObjectProperty prop)
			throws IOException {
	}

	@Override
	protected void writeClassComment(OWLClass cls) throws IOException {
	}

	@Override
	protected void writeDatatypeComment(OWLDatatype datatype)
			throws IOException {
	}

	@Override
	protected void writeIndividualComments(OWLNamedIndividual ind)
			throws IOException {
	}

	@Override
	protected void writeBanner(String name) throws IOException {
	}

}
