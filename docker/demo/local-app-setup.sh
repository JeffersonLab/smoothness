#!/bin/bash

#WILDFLY_CLI_PATH=/opt/jboss/wildfly/bin/jboss-cli.sh
WILDFLY_CLI_PATH=/mnt/c/Users/RyanS/servers/wildfly-26.1.1.Final/bin/jboss-cli.sh

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
