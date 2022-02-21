#!/bin/bash

echo "----------------------------------"
echo "| Step I: Config Keycloak client |"
echo "----------------------------------"

SECRET=9AubPamv1H9fAWo0q239EP7wPuH5ihb7
AUTH_SERVER=>http://${env.KEYCLOAK_SERVER}/auth
REALM=test-realm
RESOURCE=smoothness
DEPLOYMENT_CONFIG=principal-attribute="preferred_username",ssl-required=EXTERNAL,resource="${RESOURCE}",realm="${REALM}",auth-server-url="${AUTH_SERVER}"

/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
/subsystem=elytron-oidc-client/secure-deployment="${WAR}"/:add(${DEPLOYMENT_CONFIG})
/subsystem=elytron-oidc-client/secure-deployment="${WAR}"/credential=secret:add(secret="${SECRET}")
run-batch
EOF


echo "------------------------------------------"
echo "| Step II: Config Oracle Database client |"
echo "------------------------------------------"

ORACLE_DRIVER_PATH=${ORACLE_DRIVER_PATH:-'/ojdbc11-21.3.0.0.jar'}
DATASOURCE_NAME=smoothness

/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
module add --name=com.oracle.database.jdbc --resources=${ORACLE_DRIVER_PATH} --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle.database.jdbc)
data-source add --name=jdbc/${DATASOURCE_NAME} --driver-name=oracle --jndi-name=java:/jdbc/${DATASOURCE_NAME} --connection-url=jdbc:oracle:thin:@//${env.ORACLE_SERVER}/xepdb1 --user-name=${DB_USER} --password=${DB_PASS} --max-pool-size=3 --min-pool-size=1 --flush-strategy=EntirePool --use-fast-fail=true --blocking-timeout-wait-millis=5000 --query-timeout=30 --idle-timeout-minutes=5 --background-validation=true --background-validation-millis=30000 --validate-on-match=false --check-valid-connection-sql="select 1 from dual" --prepared-statements-cache-size=10 --share-prepared-statements=true
run-batch
EOF


echo "---------------------------------"
echo "| Step III: Config Email client |"
echo "---------------------------------"
/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
/subsystem=mail/mail-session=jlab:add(from="wildfly@jlab.org", jndi-name="java:/mail/jlab")
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp-jlab:add(host=smtpmail.jlab.org, port=25)
/subsystem=mail/mail-session=jlab/server=smtp:add(outbound-socket-binding-ref=mail-smtp-jlab)
run-batch
EOF


echo "---------------------------"
echo "| Step IV: Add Admin User |"
echo "---------------------------"
/opt/jboss/wildfly/bin/add-user.sh admin admin