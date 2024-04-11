# Library of utilities
This is a library that contains simple utilities for making working with some third tools even easier.
The utilities contained in this library are:

## Command Line utilities
Some functionality that work as facade for:
- **CommandLineRunner.executeCommand(String)**: executing a command line statement
- **CommandLineRunner.run(String)***: executing a batch file
  
## Utilities for SVN
These are the current implementations:
- **SvnDiff.exportDiff(String, String, long, long)**: get a diff file
- **SvnExport.export(long, String, String)**: exports a file from repo
- **SvnLogExtractor.extract()**: gets the logs of a file

## Utilities for Velocity
**VelocityTemplateProcessor.process(String, Map<String, Object>)** is just another level of abstraction for processing a Velocity template
