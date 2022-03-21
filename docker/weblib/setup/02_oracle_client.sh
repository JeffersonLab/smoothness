#!/bin/bash

echo "-------------------------------------------"
echo "| Setup II: Config Oracle Database client |"
echo "-------------------------------------------"

config_oracle_client() {
if [[ -z "${ORACLE_DRIVER_PATH}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_DRIVER_PATH in environment"
    return 0
fi

if [[ -z "${DATASOURCE_NAME}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide DATASOURCE_NAME in environment"
    return 0
fi

if [[ -z "${ORACLE_SERVER}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide ORACLE_SERVER in environment"
    return 0
fi

if [[ -z "${DB_SERVICE}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide DB_SERVICE in environment"
    return 0
fi

if [[ -z "${DB_USER}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide DB_USER in environment"
    return 0
fi

if [[ -z "${DB_PASS}" ]]; then
    echo "Skipping Oracle Client Setup: Must provide DB_PASS in environment"
    return 0
fi

/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
module add --name=com.oracle.database.jdbc --resources=${ORACLE_DRIVER_PATH} --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle.database.jdbc)
data-source add --name=jdbc/${DATASOURCE_NAME} --driver-name=oracle --jndi-name=java:/jdbc/${DATASOURCE_NAME} --connection-url=jdbc:oracle:thin:@//${ORACLE_SERVER}/${DB_SERVICE} --user-name=${DB_USER} --password=${DB_PASS} --max-pool-size=3 --min-pool-size=1 --flush-strategy=EntirePool --use-fast-fail=true --blocking-timeout-wait-millis=5000 --query-timeout=30 --idle-timeout-minutes=5 --background-validation=true --background-validation-millis=30000 --validate-on-match=false --check-valid-connection-sql="select 1 from dual" --prepared-statements-cache-size=10 --share-prepared-statements=true
run-batch
EOF
}

config_oracle_client