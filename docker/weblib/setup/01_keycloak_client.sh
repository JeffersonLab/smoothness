#!/bin/bash

echo "-----------------------------------"
echo "| Setup I: Config Keycloak client |"
echo "-----------------------------------"

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

/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
/subsystem=elytron-oidc-client/secure-deployment="${KEYCLOAK_WAR}"/:add(${DEPLOYMENT_CONFIG})
/subsystem=elytron-oidc-client/secure-deployment="${KEYCLOAK_WAR}"/credential=secret:add(secret="${KEYCLOAK_SECRET}")
run-batch
EOF
}

config_keycloak_client