#!/bin/bash

if [[ -z "${KEYCLOAK_HOME}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_HOME in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_SERVER_URL}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_SERVER_URL in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_ADMIN}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_ADMIN in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_ADMIN_PASSWORD}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_ADMIN_PASSWORD in environment"
    return 0
fi

echo "-----------------"
echo "| Step 1: Login |"
echo "-----------------"
${KEYCLOAK_HOME}/bin/kcadm.sh config credentials --server "${KEYCLOAK_SERVER_URL}" --realm master --user "${KEYCLOAK_ADMIN}" --password "${KEYCLOAK_ADMIN_PASSWORD}"

echo "------------------------"
echo "| Step 2: Create Realm |"
echo "------------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create realms -s realm=test-realm -s enabled=true -o

echo "------------------------"
echo "| Step 3: Create Roles |"
echo "------------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create roles -r test-realm -s name=smoothness-demo-user
${KEYCLOAK_HOME}/bin/kcadm.sh create roles -r test-realm -s name=smoothness-demo-admin

echo "-------------------------"
echo "| Step 4: Create Client |"
echo "-------------------------"
CID=$(${KEYCLOAK_HOME}/bin/kcadm.sh create clients -r test-realm -s clientId=smoothness-demo -s 'redirectUris=["https://localhost:8443/smoothness-demo/*"]' -s 'secret=yHi6W2raPmLvPXoxqMA7VWbLAA2WN0eB' -i)
echo "CID: ${CID}"

echo "------------------------"
echo "| Step 5: Create Users |"
echo "------------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create users -r test-realm -s username=user -s enabled=true
${KEYCLOAK_HOME}/bin/kcadm.sh set-password -r test-realm --username user --new-password user
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r test-realm --uusername user --rolename smoothness-demo-user

${KEYCLOAK_HOME}/bin/kcadm.sh create users -r test-realm -s username=admin -s enabled=true
${KEYCLOAK_HOME}/bin/kcadm.sh set-password -r test-realm --username admin --new-password admin
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r test-realm --uusername admin --rolename smoothness-demo-user
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r test-realm --uusername admin --rolename smoothness-demo-admin