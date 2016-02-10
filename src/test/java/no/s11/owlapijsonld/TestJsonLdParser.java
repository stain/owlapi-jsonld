package no.s11.owlapijsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class TestJsonLdParser {

	OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

	@Before
	public void register() {
		JsonLdParserFactory.register();
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
		assertTrue(ontologyManager.getOntologyFormat(ontology) instanceof JsonLdOntologyFormat);
		assertEquals("http://www.w3.org/2006/vcard/ns", ""+ontology.getOntologyID().getOntologyIRI());

		assertFalse(ontology.getClassesInSignature().isEmpty());
		assertFalse(ontology.getObjectPropertiesInSignature().isEmpty());

		IRI address = IRI.create("http://www.w3.org/2006/vcard/ns#Address");
		assertTrue(ontology.containsEntityInSignature(address));

		IRI title = IRI.create("http://www.w3.org/2006/vcard/ns#title");
		assertTrue(ontology.containsEntityInSignature(title));

	}

	private void checkHydraOntology(OWLOntology ontology) {
		assertTrue(ontologyManager.getOntologyFormat(ontology) instanceof JsonLdOntologyFormat);
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

	@Test
	public void loadIndividuals() throws Exception {
		InputStream stream = getClass().getResourceAsStream("/individuals.jsonld");
		InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
		OWLOntologyDocumentSource documentSource = new ReaderDocumentSource(reader);
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(documentSource);

		Set<OWLObjectProperty> properties = ontology.getObjectPropertiesInSignature();
		assertEquals(1, properties.size());
		OWLObjectProperty hasWife = properties.iterator().next();
		assertEquals("http://example.com/ontology#hasWife", hasWife.getIRI().toString());

		Set<OWLEntity> johnEntities = ontology.getEntitiesInSignature(IRI.create("http://example.com/ontology#John"));
		assertEquals(1, johnEntities.size());
		OWLIndividual john = (OWLIndividual) johnEntities.iterator().next();

		//OWLIndividual john = factory.getOWLNamedIndividual(IRI
	  //          .create(ontologyIRI + "#John"));

		Set<OWLIndividual> wifes = john.getObjectPropertyValues(hasWife, ontology);
		assertEquals(1, wifes.size());
		OWLNamedIndividual wife = (OWLNamedIndividual) wifes.iterator().next();
		assertEquals("http://example.com/ontology#Mary", wife.getIRI().toString());

	}



}
