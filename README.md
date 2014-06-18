# owl2jsonld

[![Build Status](https://travis-ci.org/stain/owlapi-jsonld.svg)](https://travis-ci.org/stain/owl2jsonld)


[![doi:10.5281/zenodo.10561](https://zenodo.org/badge/doi/10.5281/zenodo.10561.png)](http://dx.doi.org/10.5281/zenodo.10561)


*JSON-LD support for OWLAPI*

Add [JSON-LD](http://www.w3.org/TR/json-ld/) parser and renderer 
(read and write) support for the [OWL API](http://owlapi.sourceforge.net/). 


## Usage

If using Maven, edit `pom.xml` to include:

```xml
	<dependencies>
		<dependency>
			<groupId>org.clojars.stain</groupId>
			<artifactId>owlapi-jsonld</artifactId>
			<version>0.1.0</version>
		</dependency>
	</dependencies>
	<repositories>
		<repository>
			<id>clojars</id>
			<name>Clojars repository</name>
			<url>https://clojars.org/repo</url>			
		</repository>
	</repositories>
```

From the [example](src/test/java/no/s11/owlapijsonld/example/TestExample.java):

Reading an JSON-LD-based ontology:


```java
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		JsonLdParserFactory.register(); // Really just needed once

		IRI vcardIri = IRI.create("http://www.w3.org/2006/vcard/ns.jsonld");
		OWLOntology ontology = ontologyManager.loadOntology(vcardIri);
		ontologyManager.saveOntology(ontology, new TurtleOntologyFormat(), System.out);
```

Writing out an ontology as JSON-LD:


```java
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		JsonLdStorer.register(ontologyManager); // Needed once per ontologyManager

		IRI oaIri = IRI.create("http://www.w3.org/ns/oa.rdf");
		OWLOntology ontology = ontologyManager.loadOntology(oaIri);
		
		ontologyManager.saveOntology(ontology, new JsonLdOntologyFormat(), System.out);		
```

For further details about the OWL API, see [OWL API documentation](https://github.com/owlcs/owlapi/wiki/Documentation)


## License

Copyright © 2014 [Stian Soiland-Reyes](http://orcid.org/0000-0001-9842-9718), [University of Manchester](http://www.cs.manchester.ac.uk/).

License under the alternative of [LGPL](http://www.gnu.org/licenses/lgpl) or
[Apache license 2.0](http://www.apache.org/licenses); the same as OWL API.

