# owl2jsonld

[![Build Status](https://travis-ci.org/stain/owlapi-jsonld.svg)](https://travis-ci.org/stain/owl2jsonld)

*JSON-LD support for OWLAPI*

Add [JSON-LD](http://www.w3.org/TR/json-ld/) parser and writer 
support for the [OWL API](http://owlapi.sourceforge.net/). 


## Usage


```java
import no.s11.owlapijsonld.JSONLDParserFactory;
static { 
    JSONLDParserFactory.register() // only needed once
}
// ...
IRI ontologyIRI = IRI.create("http://www.w3.org/2006/vcard/ns.jsonld");
OWLOntology ontology = ontologyManager.loadOntology(ontologyIRI);
```

## License

Copyright © 2014 [Stian Soiland-Reyes](http://orcid.org/0000-0001-9842-9718), [University of Manchester](http://www.cs.manchester.ac.uk/).

License under the alternative of [LGPL](http://www.gnu.org/licenses/lgpl) or
[Apache license 2.0](http://www.apache.org/licenses); as OWL API.

