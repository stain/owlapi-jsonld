package no.s11.owlapijsonld;

import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class JsonLdParserFactory implements OWLParserFactory {
	
	private static class Singleton { 
		static JsonLdParserFactory instance = new JsonLdParserFactory();
	}
	
	public static JsonLdParserFactory getInstance() {
		return Singleton.instance;				
	}	
	
	public static void register() { 
		OWLParserFactoryRegistry registry = OWLParserFactoryRegistry
                .getInstance();
		if (! registry.getParserFactories().contains(getInstance())) {
			registry.registerParserFactory(getInstance());
		}
	}

	@Override
	public JsonLdParser createParser(OWLOntologyManager owlOntologyManager) {
		return new JsonLdParser();		
	}
}
