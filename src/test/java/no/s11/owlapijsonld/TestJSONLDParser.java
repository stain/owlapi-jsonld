package no.s11.owlapijsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class TestJSONLDParser {
	
	OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	
	@Before
	public void register() { 
		JSONLDParserFactory.register();
	}
	

	@Test
	public void loadHydraIRI() throws Exception {
		// Test from classpath instead, to avoid network dependencies
		//IRI ontologyIRI = IRI.create("http://www.w3.org/ns/hydra/core");
		IRI ontologyIRI = IRI.create(getClass().getResource("/hydra.jsonld"));
		OWLOntology ontology = ontologyManager.loadOntology(ontologyIRI);
		checkHydraOntology(ontology);		
	}
	
	@Test
	public void loadVcardIRI() throws Exception {
		// Test from classpath instead, to avoid network dependencies
		//IRI ontologyIRI = IRI.create("http://www.w3.org/2006/vcard/ns.jsonld");
		IRI ontologyIRI = IRI.create(getClass().getResource("/vcard.jsonld"));
		OWLOntology ontology = ontologyManager.loadOntology(ontologyIRI);
		checkVcardOntology(ontology);		
	}

	private void checkVcardOntology(OWLOntology ontology) {
		assertTrue(ontologyManager.getOntologyFormat(ontology) instanceof JSONLDOntologyFormat);
		assertEquals("http://www.w3.org/2006/vcard/ns", ""+ontology.getOntologyID().getOntologyIRI());
		
		assertFalse(ontology.getClassesInSignature().isEmpty());
		assertFalse(ontology.getObjectPropertiesInSignature().isEmpty());
		
		IRI address = IRI.create("http://www.w3.org/2006/vcard/ns#Address");
		assertTrue(ontology.containsEntityInSignature(address));
		
		IRI title = IRI.create("http://www.w3.org/2006/vcard/ns#title");
		assertTrue(ontology.containsEntityInSignature(title));
		
	}
	
	private void checkHydraOntology(OWLOntology ontology) {
		assertTrue(ontologyManager.getOntologyFormat(ontology) instanceof JSONLDOntologyFormat);
		assertEquals("http://www.w3.org/ns/hydra/core", ""+ontology.getOntologyID().getOntologyIRI());
		
		assertFalse(ontology.getClassesInSignature().isEmpty());
		assertFalse(ontology.getObjectPropertiesInSignature().isEmpty());
		
		IRI resource = IRI.create("http://www.w3.org/ns/hydra/core#Resource");
		assertTrue(ontology.containsEntityInSignature(resource));
		
		IRI operation = IRI.create("http://www.w3.org/ns/hydra/core#operation");
		assertTrue(ontology.containsEntityInSignature(operation));

	}
	
	@Test
	public void loadVcardInputStream() throws Exception {		
		InputStream stream = getClass().getResourceAsStream("/vcard.jsonld");
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(stream);
		checkVcardOntology(ontology);		
	}
	
	@Test
	public void loadVcardReader() throws Exception {		
		InputStream stream = getClass().getResourceAsStream("/vcard.jsonld");
		InputStreamReader reader = new InputStreamReader(stream, "UTF-8");		
		OWLOntologyDocumentSource documentSource = new ReaderDocumentSource(reader);
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(documentSource);
		checkVcardOntology(ontology);		
	}

	@Test
	public void loadVcardFile() throws Exception {
		InputStream stream = getClass().getResourceAsStream("/vcard.jsonld");
		Path tmpFile = Files.createTempFile("vcard", ".jsonld");
		Files.copy(stream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(tmpFile.toFile());
		checkVcardOntology(ontology);
		
	}
	
}
