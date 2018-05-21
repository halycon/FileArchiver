#!/bin/bash
pkill -f FileArchiver.jar
java -Dserver.port=8294 -Djava.security.egd=file:/dev/./urandom -Denvironment=stage -jar /opt/filearchiver/FileArchiver.jar
