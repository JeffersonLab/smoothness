#!/bin/bash

FUNCTIONS=(login
           create_prioritized_idp_flow
           set_as_realm_default)

VARIABLES=(KEYCLOAK_ADMIN
           KEYCLOAK_ADMIN_PASSWORD
           KEYCLOAK_HOME
           KEYCLOAK_ALIAS
           KEYCLOAK_REALM
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

create_prioritized_idp_flow() {
${KEYCLOAK_HOME}/bin/kcadm.sh create authentication/flows/browser/copy -r ${KEYCLOAK_REALM} \
-s newName=${KEYCLOAK_ALIAS}

EXECUTION_ID=$(${KEYCLOAK_HOME}/bin/kcadm.sh get -r ace authentication/flows/${KEYCLOAK_ALIAS}/executions | jq -r ".[] | select(.displayName == \"Identity Provider Redirector\") | .id")
${KEYCLOAK_HOME}/bin/kcadm.sh create authentication/executions/${EXECUTION_ID}/raise-priority -r ${KEYCLOAK_REALM}
}

set_as_realm_default() {
${KEYCLOAK_HOME}/bin/kcadm.sh update realms/${KEYCLOAK_REALM} -b '{"browserFlow":"'${KEYCLOAK_ALIAS}'"}'
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