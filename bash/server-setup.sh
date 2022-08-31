#!/bin/bash

if [[ -z "${WILDFLY_HOME}" ]]; then
    echo "Skipping Setup: Must provide WILDFLY_HOME in environment"
    exit 0
fi

WILDFLY_CLI_PATH=${WILDFLY_HOME}/bin/jboss-cli.sh

wildfly_start_and_wait() {
${WILDFLY_HOME}/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

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

${WILDFLY_CLI_PATH} -c <<EOF
batch
module add --name=com.oracle.database.jdbc --resources=${ORACLE_DRIVER_PATH} --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle.database.jdbc)
run-batch
EOF
}

config_admin_user() {
if [[ -z "${WILDFLY_USER}" ]]; then
    echo "Skipping Wildfly admin user Setup: Must provide WILDFLY_USER in environment"
    return 0
fi

if [[ -z "${WILDFLY_PASS}" ]]; then
    echo "Skipping Wildfly admin user Setup: Must provide WILDFLY_PASS in environment"
    return 0
fi

${WILDFLY_HOME}/bin/add-user.sh "${WILDFLY_USER}" "${WILDFLY_PASS}"
}

config_email() {
if [[ -z "${EMAIL_FROM}" ]]; then
    echo "Skipping email Setup: Must provide EMAIL_FROM in environment"
    return 0
fi

if [[ -z "${EMAIL_HOST}" ]]; then
    echo "Skipping email Setup: Must provide EMAIL_HOST in environment"
    return 0
fi

if [[ -z "${EMAIL_PORT}" ]]; then
    echo "Skipping email Setup: Must provide EMAIL_PORT in environment"
    return 0
fi

${WILDFLY_CLI_PATH} -c <<EOF
batch
/subsystem=mail/mail-session=jlab:add(from="${EMAIL_FROM}", jndi-name="java:/mail/jlab")
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp-jlab:add(host=${EMAIL_HOST}, port=${EMAIL_PORT})
/subsystem=mail/mail-session=jlab/server=smtp:add(outbound-socket-binding-ref=mail-smtp-jlab)
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

echo "---------------------------"
echo "| Setup V: Reload Wildfly |"
echo "---------------------------"

# Wildfly will complain about standalone.xml history if not reloaded
wildfly_reload

echo "--------------------------"
echo "| Setup VI: Stop Wildfly |"
echo "--------------------------"

if [[ -z "${WILDFLY_SKIP_STOP}" ]]; then
  wildfly_stop
else
  echo "Skipping Wildfly stop because WILDFLY_SKIP_STOP defined"
fi
