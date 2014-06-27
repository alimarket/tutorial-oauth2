#!/bin/bash
TOMCAT_HOME="/opt/apache-tomcat-7.0.52"

rm $TOMCAT_HOME/logs/catalina.out
rm -Rf $TOMCAT_HOME/webapps/rest-1.0-SNAPSHOT*
cp build/libs/rest-1.0-SNAPSHOT.war $TOMCAT_HOME/webapps
