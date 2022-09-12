#!/bin/bash

# Verify expected env set:
while read var; do
  [ -z "${!var}" ] && { echo "$var is not set. Exiting.."; exit 1; }
done << EOF
EMAIL_FROM
EMAIL_HOST
EMAIL_PORT
ORACLE_DRIVER_PATH
ORACLE_DRIVER_URL
WILDFLY_APP_HOME
WILDFLY_USER
WILDFLY_PASS
EOF

# Optional params
# - PROVIDED_DEPS_FILE
# - MAX_PARAM_COUNT
# - PERSISTENT_SESSIONS
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
${WILDFLY_CLI_PATH} -c "/subsystem=undertow/servlet-container=default/setting=persistent-sessions:add()"
}

config_param_limits() {
${WILDFLY_CLI_PATH} -c <<EOF
batch
/subsystem=undertow/server=default-server/http-listener=default:write-attribute(name=max-parameters,value=${MAX_PARAM_COUNT})
/subsystem=undertow/server=default-server/https-listener=https:write-attribute(name=max-parameters,value=${MAX_PARAM_COUNT})
run-batch
EOF
}

config_provided_deps() {
DIRNAME=`dirname "$0"`
SCRIPT_HOME=`cd -P "$DIRNAME"; pwd`
${SCRIPT_HOME}/provided-setup.sh ${PROVIDED_DEPS_FILE}
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
echo "| Setup II: Oracle Database Driver |"
echo "------------------------------------"

config_oracle_driver

echo "-----------------------------"
echo "| Setup III: Add Admin User |"
echo "-----------------------------"

config_admin_user

echo "---------------------------------"
echo "| Setup IV: Config Email client |"
echo "---------------------------------"

config_email

echo "---------------------------------------"
echo "| Setup V: Config Persistent Sessions |"
echo "---------------------------------------"

if [[ -z "${PERSISTENT_SESSIONS}" ]]; then
  echo "Skipping persistent sessions on redeploy because PERSISTENT_SESSIONS undefined"
else
  config_persist_sessions_on_redeploy
fi

echo "------------------------------------"
echo "| Setup VI: Config Max Param Count |"
echo "------------------------------------"

if [[ -z "${MAX_PARAM_COUNT}" ]]; then
  echo "Skipping max param count because MAX_PARAM_COUNT undefined"
else
  config_param_limits
fi

echo "-----------------------------------"
echo "| Setup VII: Config Provided Deps |"
echo "-----------------------------------"

if [[ -z "${PROVIDED_DEPS_FILE}" ]]; then
  echo "Skipping config of provided dependencies because PROVIDED_DEPS_FILE undefined"
else
  config_provided_deps
fi

echo "-----------------------------"
echo "| Setup VII: Reload Wildfly |"
echo "-----------------------------"

# Wildfly will complain about standalone.xml history if not reloaded
wildfly_reload

echo "----------------------------"
echo "| Setup VIII: Stop Wildfly |"
echo "----------------------------"

if [[ -z "${WILDFLY_SKIP_STOP}" ]]; then
  wildfly_stop
else
  echo "Skipping Wildfly stop because WILDFLY_SKIP_STOP defined"
fi
