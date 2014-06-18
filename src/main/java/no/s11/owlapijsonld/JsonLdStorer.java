package no.s11.owlapijsonld;

import java.io.IOException;
import java.io.Writer;

import org.coode.owlapi.turtle.TurtleRenderer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLOntologyStorer;
import org.semanticweb.owlapi.util.AbstractOWLOntologyStorer;

public class JsonLdStorer extends AbstractOWLOntologyStorer 
	implements OWLOntologyStorer  {
	
	private static class Singleton { 
		static JsonLdStorer instance = new JsonLdStorer();
	}
	
	public static JsonLdStorer getInstance() {
		return Singleton.instance;					
	}

	@Override
	public boolean canStoreOntology(OWLOntologyFormat format) {
		return format instanceof JsonLdOntologyFormat;
	}

	public static void register(OWLOntologyManager manager) {
		manager.removeOntologyStorer(getInstance()); // Avoid duplicate registration
		manager.addOntologyStorer(getInstance());
	}
	
	@Override
	protected void storeOntology(OWLOntologyManager manager,
			OWLOntology ontology, Writer writer, OWLOntologyFormat format)
			throws OWLOntologyStorageException {
		if (! canStoreOntology(format)) {
			throw new IllegalArgumentException("Can't store to format " + format);
		}
		
		JsonLdRenderer ren = new JsonLdRenderer(ontology, writer, format);
        try {
			ren.render();
		} catch (IOException e) {			
			throw new OWLOntologyStorageException(e);
		}
	}

	@Override
	protected void storeOntology(OWLOntology ontology, Writer writer,
			OWLOntologyFormat format) throws OWLOntologyStorageException {
		storeOntology(ontology.getOWLOntologyManager(), ontology, writer, format);		
	}

}