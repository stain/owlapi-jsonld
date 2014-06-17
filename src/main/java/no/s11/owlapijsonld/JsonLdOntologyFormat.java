package no.s11.owlapijsonld;

import org.semanticweb.owlapi.io.RDFOntologyFormat;

public class JsonLdOntologyFormat extends RDFOntologyFormat {

	static { 
		JsonLdParserFactory.register();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5143521861139705607L;

}
