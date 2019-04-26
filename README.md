# rsat-rest-java-clients

This module is a Java Client for the Regulatory Sequence Analysis Tools (RSAT) 
[RESTful API](http://rsat-tagc.univ-mrs.fr/rest/). 

Currently, only one tool is supported with a Java Client, namely the 
`fetch-sequences` tool. This tool is used to retrieve genome sequences for a 
set of coordinates specified in a bed file (either from a resource URL through 
the `GET` and `POST` methods or from a local .bed file through `POST`).

## Install

Tested with maven version 3.6.0, compiled with Java 8
```
mvn clean install
```

## Examples

- When using no arguments, you get the list of available parameters:
```
java -cp target/java.clients-1.0-jar-with-dependencies.jar clients.FetchSequencesClient 

Options preceded by an asterisk are required.
Usage: clients.FetchSequencesClient [options]
  Options:
  * --requestMethod, -m
      The HTTP request method: GET or POST
    --responseContentType, -c
      The response content type: json or text
      Default: json
    --genome, -g
      Genome version (e.g. mm9, hg19)
      Default: mm9
    --header, -h
      Format for sequence headers: UCSC or galaxy
      Default: galaxy
    --resourceURL, -u
      URL file resource
    --resourceBedFile, -f
      File in .bed format
```

- Using the `GET` HTTP method to retrieve the sequences from a URL resource:
```
java -cp target/java.clients-1.0-jar-with-dependencies.jar clients.FetchSequencesClient -m=GET -c=json -u=http://rsat-tagc.univ-mrs.fr/rsat/demo_files/fetch-sequences_Schmidt_2011_mm9_CEBPA_SWEMBL_R0.12_702peaks.bed -g=mm9 -h=galaxy
```

- Using the `POST` HTTP method to retrieve the sequences from a URL resource:
```
java -cp target/java.clients-1.0-jar-with-dependencies.jar clients.FetchSequencesClient -m=POST -c=text -u=http://rsat-tagc.univ-mrs.fr/rsat/demo_files/fetch-sequences_Schmidt_2011_mm9_CEBPA_SWEMBL_R0.12_702peaks.bed -g=mm9 -h=galaxy
```

- Using the `POST` HTTP method to retrieve the sequences from a local .bed file 
resource:
```
java -cp target/java.clients-1.0-jar-with-dependencies.jar clients.FetchSequencesClient -m=POST -c=json -f=src/main/resources/test.bed -g=mm9 -h=galaxy
```
