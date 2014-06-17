package no.s11.owlapijsonld;

import org.semanticweb.owlapi.io.RDFOntologyFormat;

public class JSONLDOntologyFormat extends RDFOntologyFormat {

	static { 
		JSONLDParserFactory.register();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5143521861139705607L;

}
