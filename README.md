## FileArchiver
This service application project act as middleware for IBM FiletNet repository.

## Code style

[![eclipse-cd](https://img.shields.io/badge/code%20style-standard-brightgreen.svg?style=flat)](https://github.com/checkstyle/eclipse-cs)
 

## Tech/framework used
Spring Boot , Jax-WS, RabbitMQ , MySQL
Swagger for Documentation

<b>Built with</b>
- [Gradle](https://gradle.org)

## Installation

RabbitMQ have to be up installed and running with either package manager , manually or up with a docker image
MySQL have to be up installed and running with either package manager , manually or up with a docker image 
(database related info in mysql_schema_ddl directory)
Gradle have to be installed

gradle build
java -Dserver.port=8294 -Djava.security.egd=file:/dev/./urandom -Denvironment=devel -jar build/libs/FileArchiver.jar

## API Reference

Swagger UI
http://localhost:9294/swagger-ui.html#/

Soap WSDL
http://localhost:9294/FileArchiverSoap?wsdl


© [Volkan Cetin]()
