#!/bin/bash

FUNCTIONS=(login
           create_ldap_storage_provider
           fix_first_name_mapper
           create_role_mapper)

VARIABLES=(KEYCLOAK_ADMIN
           KEYCLOAK_ADMIN_PASSWORD
           KEYCLOAK_HOME
           KEYCLOAK_REALM
           KEYCLOAK_SERVER_URL
           KEYCLOAK_SERVER_PRINCIPLE
           KEYCLOAK_BIND_CREDENTIAL
           KEYCLOAK_USERS_DN
           KEYCLOAK_DEBUG
           KEYCLOAK_LDAP_CONNECTION_URL
           KEYCLOAK_KEYTAB
           KEYCLOAK_KERBEROS_REALM)

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

create_ldap_storage_provider() {
${KEYCLOAK_HOME}/bin/kcadm.sh create components -r ${KEYCLOAK_REALM} -s parentId=${KEYCLOAK_REALM} \
-s id=${KEYCLOAK_REALM}-ldap-provider -s name=${KEYCLOAK_REALM}-ldap-provider \
-s providerId=ldap -s providerType=org.keycloak.storage.UserStorageProvider \
-s config.debug=${KEYCLOAK_DEBUG} \
-s config.authType='["simple"]' \
-s config.vendor='["other"]' \
-s config.priority='["1"]' \
-s config.connectionUrl=${KEYCLOAK_LDAP_CONNECTION_URL} \
-s config.editMode='["READ_ONLY"]' \
-s config.usersDn=${KEYCLOAK_USERS_DN} \
-s config.serverPrincipal=${KEYCLOAK_SERVER_PRINCIPLE} \
-s config.bindDn=${KEYCLOAK_BIND_DN} \
-s config.bindCredential=${KEYCLOAK_BIND_CREDENTIAL} \
-s 'config.fullSyncPeriod=["-1"]' \
-s 'config.changedSyncPeriod=["-1"]' \
-s 'config.cachePolicy=["NO_CACHE"]' \
-s config.evictionDay=[] \
-s config.evictionHour=[] \
-s config.evictionMinute=[] \
-s config.maxLifespan=[] \
-s config.importEnabled='["false"]' \
-s 'config.batchSizeForSync=["1000"]' \
-s 'config.syncRegistrations=["false"]' \
-s 'config.usernameLDAPAttribute=["uid"]' \
-s 'config.rdnLDAPAttribute=["uid"]' \
-s 'config.uuidLDAPAttribute=["uid"]' \
-s 'config.userObjectClasses=["inetOrgPerson, organizationalPerson"]' \
-s 'config.searchScope=["1"]' \
-s 'config.useTruststoreSpi=["ldapsOnly"]' \
-s 'config.connectionPooling=["true"]' \
-s 'config.pagination=["true"]' \
-s 'config.allowKerberosAuthentication=["true"]' \
-s config.keyTab=${KEYCLOAK_KEYTAB} \
-s config.kerberosRealm=${KEYCLOAK_KERBEROS_REALM} \
-s 'config.useKerberosForPasswordAuthentication=["true"]'
}

fix_first_name_mapper() {
MAPPER_ID=`${KEYCLOAK_HOME}/bin/kcadm.sh get components -r ${KEYCLOAK_REALM} -q name='first name' --fields id | jq -r .[0].id`
${KEYCLOAK_HOME}/bin/kcadm.sh update components/${MAPPER_ID} -r ${KEYCLOAK_REALM} -s 'config."ldap.attribute"=["givenName"]'
}

create_role_mapper() {

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