#!/bin/bash

FUNCTIONS=(login
           create_client)

VARIABLES=(KEYCLOAK_ADMIN
           KEYCLOAK_ADMIN_PASSWORD
           KEYCLOAK_HOME
           KEYCLOAK_REALM
           KEYCLOAK_CLIENT_NAME
           KEYCLOAK_REDIRECT_URIS
           KEYCLOAK_SERVER_URL)

if [[ $# -eq 0 ]] ; then
    echo "Usage: $0 [var file] <optional function>"
    echo "The var file arg should be the path to a file with bash variables that will be sourced."
    echo "The optional function name arg if provided is the sole function to call, else all functions are invoked sequentially."
    printf 'Variables: '
    printf '%s ' "${VARIABLES[@]}"
    printf '\n'
    printf 'Functions: '
    printf '%s ' "${FUNCTIONS[@]}"
    printf '\n'
    exit 0
fi

if [ ! -z "$1" ] && [ -f "$1" ]
then
echo "Loading environment $1"
. $1
fi

# Verify expected env set:
for i in "${!VARIABLES[@]}"; do
  var=${VARIABLES[$i]}
  [ -z "${!var}" ] && { echo "$var is not set. Exiting."; exit 1; }
done


login() {
${KEYCLOAK_HOME}/bin/kcadm.sh config credentials --server "${KEYCLOAK_SERVER_URL}" --realm master --user "${KEYCLOAK_ADMIN}" --password "${KEYCLOAK_ADMIN_PASSWORD}"
}

create_client() {
${KEYCLOAK_HOME}/bin/kcadm.sh create clients -r "${KEYCLOAK_REALM}" -s clientId=${KEYCLOAK_CLIENT_NAME} -s id=${KEYCLOAK_CLIENT_NAME} -s enabled=true -s serviceAccountsEnabled=true -s redirectUris=${KEYCLOAK_REDIRECT_URIS} -s secret=${KEYCLOAK_SECRET}
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r "${KEYCLOAK_REALM}" --uusername service-account-${KEYCLOAK_CLIENT_NAME} --cclientid realm-management --rolename view-users
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r "${KEYCLOAK_REALM}" --uusername service-account-${KEYCLOAK_CLIENT_NAME} --cclientid realm-management --rolename view-authorization
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r "${KEYCLOAK_REALM}" --uusername service-account-${KEYCLOAK_CLIENT_NAME} --cclientid realm-management --rolename view-realm
}

if [ ! -z "$2" ]
then
  echo "------------------------"
  echo "$2"
  echo "------------------------"
  $2
else
for i in "${!FUNCTIONS[@]}"; do
  echo "------------------------"
  echo "${FUNCTIONS[$i]}"
  echo "------------------------"
  ${FUNCTIONS[$i]};
done
fi