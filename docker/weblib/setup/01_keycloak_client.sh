#!/bin/bash

echo "-----------------------------------"
echo "| Setup I: Config Keycloak client |"
echo "-----------------------------------"

config_keycloak_client() {
if [[ -z "${WAR}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide WAR in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_SERVER}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide KEYCLOAK_SERVER in environment"
    return 0
fi

if [[ -z "${SECRET}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide SECRET in environment"
    return 0
fi

if [[ -z "${REALM}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide REALM in environment"
    return 0
fi

if [[ -z "${RESOURCE}" ]]; then
    echo "Skipping Keycloak Client Setup: Must provide RESOURCE in environment"
    return 0
fi


AUTH_SERVER="http://${KEYCLOAK_SERVER}/auth"
DEPLOYMENT_CONFIG=principal-attribute="preferred_username",ssl-required=EXTERNAL,resource="${RESOURCE}",realm="${REALM}",auth-server-url="${AUTH_SERVER}"

/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
/subsystem=elytron-oidc-client/secure-deployment="${WAR}"/:add(${DEPLOYMENT_CONFIG})
/subsystem=elytron-oidc-client/secure-deployment="${WAR}"/credential=secret:add(secret="${SECRET}")
run-batch
EOF
}

config_keycloak_client