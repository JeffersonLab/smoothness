#!/bin/bash

if [[ -z "${WILDFLY_CLI_PATH}" ]]; then
    echo "Skipping Setup: Must provide WILDFLY_CLI_PATH in environment"
    exit 0
fi

config_keycloak_client() {
if [[ -z "${KEYCLOAK_WAR}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide WAR in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_SERVER_URL}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide KEYCLOAK_SERVER_URL in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_SECRET}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide KEYCLOAK_SECRET in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_REALM}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide KEYCLOAK_REALM in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_RESOURCE}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide KEYCLOAK_RESOURCE in environment"
    return 0
fi

DEPLOYMENT_CONFIG=principal-attribute="preferred_username",ssl-required=EXTERNAL,resource="${KEYCLOAK_RESOURCE}",realm="${KEYCLOAK_REALM}",auth-server-url=${KEYCLOAK_SERVER_URL}

${WILDFLY_CLI_PATH} -c <<EOF
batch
/subsystem=elytron-oidc-client/secure-deployment="${KEYCLOAK_WAR}"/:add(${DEPLOYMENT_CONFIG})
/subsystem=elytron-oidc-client/secure-deployment="${KEYCLOAK_WAR}"/credential=secret:add(secret="${KEYCLOAK_SECRET}")
run-batch
EOF
}

config_oracle_client() {
if [[ -z "${ORACLE_DATASOURCE}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_DATASOURCE in environment"
    return 0
fi

if [[ -z "${ORACLE_SERVER}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_SERVER in environment"
    return 0
fi

if [[ -z "${ORACLE_SERVICE}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_SERVICE in environment"
    return 0
fi

if [[ -z "${ORACLE_USER}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_USER in environment"
    return 0
fi

if [[ -z "${ORACLE_PASS}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_PASS in environment"
    return 0
fi

${WILDFLY_CLI_PATH} -c <<EOF
batch
data-source add --name=jdbc/${ORACLE_DATASOURCE} --driver-name=oracle --jndi-name=java:/jdbc/${ORACLE_DATASOURCE} --connection-url=jdbc:oracle:thin:@//\${ORACLE_SERVER}/${ORACLE_SERVICE} --user-name=${ORACLE_USER} --password=${ORACLE_PASS} --max-pool-size=3 --min-pool-size=1 --flush-strategy=EntirePool --use-fast-fail=true --blocking-timeout-wait-millis=5000 --query-timeout=30 --idle-timeout-minutes=5 --background-validation=true --background-validation-millis=30000 --validate-on-match=false --check-valid-connection-sql="select 1 from dual" --prepared-statements-cache-size=10 --share-prepared-statements=true
run-batch
EOF
}

echo "-----------------------------------"
echo "| Setup I: Config Keycloak client |"
echo "-----------------------------------"

config_keycloak_client

echo "-------------------------------------------"
echo "| Setup II: Config Oracle Database client |"
echo "-------------------------------------------"

config_oracle_client
