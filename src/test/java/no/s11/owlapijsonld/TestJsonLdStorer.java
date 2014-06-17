package no.s11.owlapijsonld;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class TestJsonLdStorer {
	
	private static OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	
	@Before
	public void register() {
		JsonLdStorer.register(ontologyManager);
	}
	
	@Test
	public void storeJsonLd() throws Exception {
		OWLOntology ont = ontologyManager.createOntology();
		ontologyManager.saveOntology(ont, new JsonLdOntologyFormat(), System.out);
		
	}
}
