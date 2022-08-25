#!/bin/bash

# Linux Container
#WILDFLY_HOME=/opt/jboss/wildfly

# Windows WSL2
#WILDFLY_HOME=/mnt/c/Users/RyanS/servers/wildfly-26.1.1.Final

# MacOS
WILDFLY_HOME=/Users/ryans/Servers/wildfly-26.1.1.Final

KEYCLOAK_WAR=smoothness-demo.war
#KEYCLOAK_SERVER_URL=http://keycloak:8080
KEYCLOAK_SERVER_URL=http://localhost:8081
KEYCLOAK_SECRET=yHi6W2raPmLvPXoxqMA7VWbLAA2WN0eB
KEYCLOAK_REALM=test-realm
KEYCLOAK_RESOURCE=smoothness-demo

ORACLE_DATASOURCE=smoothness
#ORACLE_SERVER=oracle:1521
ORACLE_SERVER=localhost:1521
ORACLE_SERVICE=xepdb1
ORACLE_USER=SMOOTHNESS_OWNER
ORACLE_PASS=password

. app-setup.sh
