package no.s11.owlapijsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonLdStorer {
	
	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	@Before
	public void register() {
		JsonLdStorer.register(manager);
	}
	
	@Test
	public void storeJsonLd() throws Exception {
		OWLOntology ont = makeOntology();
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		manager.saveOntology(ont, new JsonLdOntologyFormat(), out);
		String jsonStr = out.toString("UTF-8");
		System.out.println(jsonStr);
		
		JsonNode json = new ObjectMapper().readTree(out.toByteArray());
		//System.out.println(json);
		
		// Was the context included correctly?		
		JsonNode context = json.get("@context");
		assertEquals("http://www.w3.org/2002/07/owl#",
				context.get("owl").asText()); 
		assertEquals("@id", context.get("owl:sameAs").get("@type").asText());
		
		JsonNode graph = json.get("@graph");
		assertTrue(graph.isArray());
		JsonNode intersection = graph.findValue("owl:intersectionOf");
		assertTrue(intersection.toString().contains("http://example.com/ontology#Person"));

		// We'll assume for now that there is 
		// no @base or prefix for http://example.com/ontology# 
		JsonNode john = graph.findValue("http://example.com/ontology#hasWife");
		assertEquals("http://example.com/ontology#Mary", john.get("@id").asText());
		
	}

	private OWLOntology makeOntology() throws OWLOntologyCreationException {
		OWLDataFactory factory = manager.getOWLDataFactory();

		IRI ontologyIRI = IRI.create("http://example.com/ontology");
		OWLOntology ont = manager.createOntology(ontologyIRI);
		
		OWLClass person = factory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
				
		OWLClass woman = factory.getOWLClass(IRI.create(ontologyIRI + "#Woman"));
		OWLClass man = factory.getOWLClass(IRI.create(ontologyIRI + "#Man"));

		// These are not needed OWL wise, but are here to ensure we get a @list on subclassOf
		manager.addAxiom(ont,  factory.getOWLSubClassOfAxiom(woman, person));
		manager.addAxiom(ont,  factory.getOWLSubClassOfAxiom(man, person));

		OWLClass malePhenotype = factory.getOWLClass(IRI.create(ontologyIRI + "#MalePhenotype"));
		OWLClass femalePhenotype = factory.getOWLClass(IRI.create(ontologyIRI + "#FemalePhenotype"));

		OWLObjectIntersectionOf malePerson = factory.getOWLObjectIntersectionOf(malePhenotype, person);
		manager.addAxiom(ont, factory.getOWLSubClassOfAxiom(man, malePerson));
		
		OWLObjectIntersectionOf femalePerson = factory.getOWLObjectIntersectionOf(femalePhenotype, person);
		manager.addAxiom(ont, factory.getOWLSubClassOfAxiom(woman, femalePerson));
		
		manager.addAxiom(ont, factory.getOWLEquivalentClassesAxiom(man, malePhenotype, person));
	    
		OWLIndividual john = factory.getOWLNamedIndividual(IRI
	            .create(ontologyIRI + "#John"));
	    manager.addAxiom(ont, factory.getOWLClassAssertionAxiom(malePhenotype, john));
	    manager.addAxiom(ont, factory.getOWLClassAssertionAxiom(person, john));
	    

	    OWLIndividual mary = factory.getOWLNamedIndividual(IRI
	            .create(ontologyIRI + "#Mary"));
	    manager.addAxiom(ont, factory.getOWLClassAssertionAxiom(person, mary));

	    
	    OWLObjectProperty hasWife = factory.getOWLObjectProperty(IRI
	    		.create(ontologyIRI + "#hasWife"));
	    manager.addAxiom(ont,  factory.getOWLObjectPropertyRangeAxiom(hasWife, woman));
	    
	    manager.addAxiom(ont, factory
	            .getOWLObjectPropertyAssertionAxiom(hasWife, john, mary));
		
	    
	    // Because we are good guys..
	    OWLAnnotationProperty isDefinedBy = factory.getRDFSIsDefinedBy();
	    
	    for (OWLObject obj : Arrays.asList(person, 
	    		woman, man, malePhenotype, femalePhenotype, john, mary, hasWife)) {
	    	manager.addAxiom(ont, 
		    		factory.getOWLAnnotationAssertionAxiom(isDefinedBy,
		    				((OWLNamedObject)obj).getIRI(), 
		    				ont.getOntologyID().getOntologyIRI()));
		    	
	    }
		return ont;
	}
}
