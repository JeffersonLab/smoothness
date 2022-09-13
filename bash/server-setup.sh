#!/bin/bash

FUNCTIONS=(wildfly_start_and_wait
           config_oracle_driver
           config_admin_user
           config_email
           config_persist_sessions_on_redeploy
           config_param_limits
           config_provided_deps
           wildfly_reload
           wildfly_stop)

VARIABLES=(EMAIL_FROM
           EMAIL_HOST
           EMAIL_PORT
           ORACLE_DRIVER_PATH
           ORACLE_DRIVER_URL
           WILDFLY_APP_HOME
           WILDFLY_USER
           WILDFLY_PASS)

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

# Optional params
# - PROVIDED_DEPS_FILE
# - MAX_PARAM_COUNT
# - PERSISTENT_SESSIONS
# - WILDFLY_SKIP_START
# - WILDFLY_SKIP_STOP

WILDFLY_CLI_PATH=${WILDFLY_APP_HOME}/bin/jboss-cli.sh

wildfly_start_and_wait() {
if [[ ! -z "${WILDFLY_SKIP_START}" ]]; then
  echo "Skipping Wildfly start because WILDFLY_SKIP_START defined"
  return 0
fi

${WILDFLY_APP_HOME}/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

until curl http://localhost:8080 -sf -o /dev/null;
do
  echo $(date) " Still waiting for Wildfly to start..."
  sleep 5
done

echo $(date) " Wildfly started!"
}

config_oracle_driver() {
if [[ -z "${ORACLE_DRIVER_PATH}" ]]; then
    echo "Skipping Oracle Driver Setup: Must provide ORACLE_DRIVER_PATH in environment"
    return 0
fi

wget -O "${ORACLE_DRIVER_PATH}" "${ORACLE_DRIVER_URL}"

${WILDFLY_CLI_PATH} -c <<EOF
batch
module add --name=com.oracle.database.jdbc --resources=${ORACLE_DRIVER_PATH} --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle.database.jdbc)
run-batch
EOF
}

config_admin_user() {
${WILDFLY_APP_HOME}/bin/add-user.sh "${WILDFLY_USER}" "${WILDFLY_PASS}"
}

config_email() {
${WILDFLY_CLI_PATH} -c <<EOF
batch
/subsystem=mail/mail-session=jlab:add(from="${EMAIL_FROM}", jndi-name="java:/mail/jlab")
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp-jlab:add(host=${EMAIL_HOST}, port=${EMAIL_PORT})
/subsystem=mail/mail-session=jlab/server=smtp:add(outbound-socket-binding-ref=mail-smtp-jlab)
run-batch
EOF
}

config_persist_sessions_on_redeploy() {
if [[ -z "${PERSISTENT_SESSIONS}" ]]; then
  echo "Skipping persistent sessions on redeploy because PERSISTENT_SESSIONS undefined"
  return 0
fi

${WILDFLY_CLI_PATH} -c "/subsystem=undertow/servlet-container=default/setting=persistent-sessions:add()"
}

config_param_limits() {
if [[ -z "${MAX_PARAM_COUNT}" ]]; then
  echo "Skipping max param count because MAX_PARAM_COUNT undefined"
  return 0
fi

${WILDFLY_CLI_PATH} -c <<EOF
batch
/subsystem=undertow/server=default-server/http-listener=default:write-attribute(name=max-parameters,value=${MAX_PARAM_COUNT})
/subsystem=undertow/server=default-server/https-listener=https:write-attribute(name=max-parameters,value=${MAX_PARAM_COUNT})
run-batch
EOF
}

config_provided_deps() {
if [[ -z "${PROVIDED_DEPS_FILE}" ]]; then
  echo "Skipping config of provided dependencies because PROVIDED_DEPS_FILE undefined"
  return 0
fi

DIRNAME=`dirname "$0"`
SCRIPT_HOME=`cd -P "$DIRNAME"; pwd`
${SCRIPT_HOME}/provided-setup.sh ${PROVIDED_DEPS_FILE}
}

wildfly_reload() {
${WILDFLY_CLI_PATH} -c reload
}

wildfly_stop() {
if [[ ! -z "${WILDFLY_SKIP_STOP}" ]]; then
  echo "Skipping Wildfly stop because WILDFLY_SKIP_STOP defined"
  return 0
fi

${WILDFLY_CLI_PATH} -c shutdown
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





