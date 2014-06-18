package no.s11.owlapijsonld;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class TestJsonLdStorer {
	
	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	@Before
	public void register() {
		JsonLdStorer.register(manager);
	}
	
	@Test
	public void storeJsonLd() throws Exception {
		OWLDataFactory factory = manager.getOWLDataFactory();

		IRI ontologyIRI = IRI.create("http://example.com/ontology");
		OWLOntology ont = manager.createOntology(ontologyIRI);
		
		OWLClass person = factory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
				
		// TODO: Make this politically correct
		OWLClass man = factory.getOWLClass(IRI.create(ontologyIRI + "#Man"));
		OWLClass woman = factory.getOWLClass(IRI.create(ontologyIRI + "#Woman"));
		manager.addAxiom(ont,  factory.getOWLSubClassOfAxiom(man, person));
		manager.addAxiom(ont,  factory.getOWLSubClassOfAxiom(woman, person));

	    
		OWLIndividual john = factory.getOWLNamedIndividual(IRI
	            .create(ontologyIRI + "#John"));
	    manager.addAxiom(ont, factory.getOWLClassAssertionAxiom(man, john));
	    

	    OWLIndividual mary = factory.getOWLNamedIndividual(IRI
	            .create(ontologyIRI + "#Mary"));
	    manager.addAxiom(ont, factory.getOWLClassAssertionAxiom(person, mary));
	    
	    OWLObjectProperty hasWife = factory.getOWLObjectProperty(IRI
	    		.create(ontologyIRI + "#hasWife"));
	    manager.addAxiom(ont,  factory.getOWLObjectPropertyRangeAxiom(hasWife, woman));
	    
	    manager.addAxiom(ont, factory
	            .getOWLObjectPropertyAssertionAxiom(hasWife, john, mary));
		
		manager.saveOntology(ont, new JsonLdOntologyFormat(), System.out);
		
	}
}
