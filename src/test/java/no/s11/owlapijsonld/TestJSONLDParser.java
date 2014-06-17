package no.s11.owlapijsonld;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class TestJSONLDParser {
	
	OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	
	@Test
	public void load() throws Exception {		
		IRI ontologyIRI = IRI.create("http://www.w3.org/2006/vcard/ns.jsonld");
		OWLOntology ontology = ontologyManager.loadOntology(ontologyIRI);
		assertTrue(ontologyManager.getOntologyFormat(ontology) instanceof JSONLDOntologyFormat);
		assertEquals("http://www.w3.org/2006/vcard/ns", ontology.getOntologyID().getOntologyIRI());
		
		IRI addressIRI = IRI.create("http://www.w3.org/2006/vcard/ns#Address");
		assertFalse(ontology.getClassesInSignature().isEmpty());
		assertTrue(ontology.containsEntityInSignature(addressIRI));
		
	}
}
