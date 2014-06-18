package no.s11.owlapijsonld;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class TestExample {

	@Test
	public void convertJsonLdOWLToTurtle() throws Exception {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		JsonLdParserFactory.register(); // Really just needed once

		IRI vcardIri = IRI.create("http://www.w3.org/2006/vcard/ns.jsonld");
		OWLOntology ontology = ontologyManager.loadOntology(vcardIri);
		ontologyManager.saveOntology(ontology, new TurtleOntologyFormat(), System.out);
	}

	@Test
	public void convertRdfXMLToJsonLd() throws Exception {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		JsonLdStorer.register(ontologyManager); // Needed once per ontologyManager

		IRI oaIri = IRI.create("http://www.w3.org/ns/oa.rdf");
		OWLOntology ontology = ontologyManager.loadOntology(oaIri);
		
		ontologyManager.saveOntology(ontology, new JsonLdOntologyFormat(), System.out);		
	}
	
}
