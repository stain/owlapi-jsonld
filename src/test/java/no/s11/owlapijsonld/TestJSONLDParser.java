package no.s11.owlapijsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
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
	public void loadVcardIRI() throws Exception {
		// Test from classpath instead, to avoid network dependencies
		//IRI ontologyIRI = IRI.create("http://www.w3.org/2006/vcard/ns.jsonld");
		IRI ontologyIRI = IRI.create(getClass().getResource("/vcard.jsonld"));
		System.out.println(ontologyIRI);
		
		OWLOntology ontology = ontologyManager.loadOntology(ontologyIRI);
		checkVcardOntology(ontology);		
	}

	private void checkVcardOntology(OWLOntology ontology) {
		assertTrue(ontologyManager.getOntologyFormat(ontology) instanceof JSONLDOntologyFormat);
		assertEquals("http://www.w3.org/2006/vcard/ns", ""+ontology.getOntologyID().getOntologyIRI());
		
		assertFalse(ontology.getClassesInSignature().isEmpty());
		
		IRI addressIRI = IRI.create("http://www.w3.org/2006/vcard/ns#Address");
		assertTrue(ontology.containsEntityInSignature(addressIRI));
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
