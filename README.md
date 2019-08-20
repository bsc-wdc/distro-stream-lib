<!-- Automatic builds status -->
<!-- [![Build Status](https://travis-ci.org/XX)](https://travis-ci.org/XX) -->

<!-- Codacy -->
<!-- [![Codacy grade](XX)](XX) -->

<!-- [![Codacy coverage](XX)](XX) -->

<!-- Codecov -->
<!-- [![codecov](XX)](XX) -->

<!-- Maven central packages version -->
<!-- [![Maven Central](https://maven-badges.herokuapp.com/maven-central/XX)](https://maven-badges.herokuapp.com/maven-central/XX) -->

<!-- Java DOC status -->
<!-- [![Javadocs](http://javadoc.io/badge/XX.svg)](http://javadoc.io/doc/XX) -->

<!-- Main Repository language -->
[![Language](https://img.shields.io/badge/language-java-brightgreen.svg)](https://img.shields.io/badge/language-java-brightgreen.svg)

<!-- Repository License -->
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://compss.bsc.es/gitlab/project/distro-stream-lib/blob/master/LICENSE)


# Distributed Stream Library

The implementation includes:
* DistroStream: Stream interface to handle stream accesses homogeneously regardless of the stream engine backing it (Python and Java).
    * ObjectDistroStream: Implementation of the interface for object streams built on top of Kafka (Java).
    * FileDistroStream: Implementation of the interface for file streams that monitors the creation of files inside a directory (Python and Java).
* Client: Acts as a broker on behalf of the application and interacts with the corresponding stream backend (Python and Java).
* Server: Centralized server to manage the streams metadata and coordinate the accesses (Java).
* Test: Basic integration tests.

---

## Table of Contents

* [Contributing](#contributing)
* [Author](#author)
* [Disclaimer](#disclaimer)
* [License](#license)

---

## Contributing

All kinds of contributions are welcome. Please do not hesitate to open a new issue, submit a pull request or contact the author if necessary. 
 

## Author

Cristián Ramón-Cortés Vilarrodona <cristian.ramoncortes(at)bsc.es> ([Personal Website][cristian])

This work is supervised by:
- Rosa M. Badia ([BSC][bsc])
- Jorge Ejarque ([BSC][bsc])
- Francesc Lordan ([BSC][bsc])


## Disclaimer

This work is part of a PhD Thesis at the [Workflows and Distributed Computing Team][wdc-bsc] at [BSC][bsc] and is still under development. 


## License

Licensed under the [Apache 2.0 License][apache-2]


[wdc-bsc]: https://www.bsc.es/discover-bsc/organisation/scientific-structure/workflows-and-distributed-computing
[bsc]: https://www.bsc.es/
[cristian]: https://cristianrcv.netlify.com/

[apache-2]: http://www.apache.org/licenses/LICENSE-2.0
