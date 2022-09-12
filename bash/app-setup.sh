#!/bin/bash

if [ ! -z "$1" ] && [ -f "$1" ]
then
echo "$1 exists, loading"
. $1
fi

# Verify expected env set:
while read var; do
  [ -z "${!var}" ] && { echo "$var is not set. Exiting.."; exit 1; }
done << EOF
KEYCLOAK_REALM
KEYCLOAK_RESOURCE
KEYCLOAK_SECRET
KEYCLOAK_SERVER_URL
KEYCLOAK_WAR
ORACLE_DATASOURCE
ORACLE_PASS
ORACLE_SERVER
ORACLE_SERVICE
ORACLE_USER
WILDFLY_APP_HOME
EOF

# Optional params
# - WILDFLY_SKIP_START
# - WILDFLY_SKIP_STOP

WILDFLY_CLI_PATH=${WILDFLY_APP_HOME}/bin/jboss-cli.sh

wildfly_start_and_wait() {
${WILDFLY_APP_HOME}/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

until curl http://localhost:8080 -sf -o /dev/null;
do
  echo $(date) " Still waiting for Wildfly to start..."
  sleep 5
done

echo $(date) " Wildfly started!"
}

wildfly_reload() {
${WILDFLY_CLI_PATH} -c reload
}

wildfly_stop() {
${WILDFLY_CLI_PATH} -c shutdown
}

config_keycloak_client() {
DEPLOYMENT_CONFIG=principal-attribute="preferred_username",ssl-required=EXTERNAL,resource="${KEYCLOAK_RESOURCE}",realm="${KEYCLOAK_REALM}",auth-server-url=${KEYCLOAK_SERVER_URL}

${WILDFLY_CLI_PATH} -c <<EOF
batch
/subsystem=elytron-oidc-client/secure-deployment="${KEYCLOAK_WAR}"/:add(${DEPLOYMENT_CONFIG})
/subsystem=elytron-oidc-client/secure-deployment="${KEYCLOAK_WAR}"/credential=secret:add(secret="${KEYCLOAK_SECRET}")
run-batch
EOF
}

config_oracle_client() {
${WILDFLY_CLI_PATH} -c <<EOF
batch
data-source add --name=jdbc/${ORACLE_DATASOURCE} --driver-name=oracle --jndi-name=java:/jdbc/${ORACLE_DATASOURCE} --connection-url=jdbc:oracle:thin:@//${ORACLE_SERVER}/${ORACLE_SERVICE} --user-name=${ORACLE_USER} --password=${ORACLE_PASS} --max-pool-size=3 --min-pool-size=1 --flush-strategy=EntirePool --use-fast-fail=true --blocking-timeout-wait-millis=5000 --query-timeout=30 --idle-timeout-minutes=5 --background-validation=true --background-validation-millis=30000 --validate-on-match=false --check-valid-connection-sql="select 1 from dual" --prepared-statements-cache-size=10 --share-prepared-statements=true
run-batch
EOF
}

echo "--------------------------"
echo "| Setup I: Start Wildfly |"
echo "--------------------------"

if [[ -z "${WILDFLY_SKIP_START}" ]]; then
  wildfly_start_and_wait
else
  echo "Skipping Wildfly start because WILDFLY_SKIP_START defined"
fi


echo "------------------------------------"
echo "| Setup II: Config Keycloak client |"
echo "------------------------------------"

config_keycloak_client

echo "--------------------------------------------"
echo "| Setup III: Config Oracle Database client |"
echo "--------------------------------------------"

config_oracle_client

echo "----------------------------"
echo "| Setup IV: Reload Wildfly |"
echo "----------------------------"

# Wildfly will complain about standalone.xml history if not reloaded
wildfly_reload

echo "-------------------------"
echo "| Setup V: Stop Wildfly |"
echo "-------------------------"

if [[ -z "${WILDFLY_SKIP_STOP}" ]]; then
  wildfly_stop
else
  echo "Skipping Wildfly stop because WILDFLY_SKIP_STOP defined"
fi
