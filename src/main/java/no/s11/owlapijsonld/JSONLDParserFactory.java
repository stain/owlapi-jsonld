package no.s11.owlapijsonld;

import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class JSONLDParserFactory implements OWLParserFactory {
	
	private static class Singleton { 
		static JSONLDParserFactory instance = new JSONLDParserFactory();
	}
	
	public static void register() { 
		OWLParserFactoryRegistry registry = OWLParserFactoryRegistry
                .getInstance();
		if (! registry.getParserFactories().contains(getInstance())) {
			registry.registerParserFactory(getInstance());
		}		
	}

	private static OWLParserFactory getInstance() {
		return Singleton.instance;				
	}	

	@Override
	public OWLParser createParser(OWLOntologyManager owlOntologyManager) {
		return new JSONLDParser();		
	}
}
