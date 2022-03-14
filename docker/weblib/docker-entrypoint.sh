#!/bin/bash

echo "----------------------------------------------------"
echo "| Step 1: Waiting for Oracle DB to start listening |"
echo "----------------------------------------------------"
if [[ -n ${DB_USER} && -n ${DB_PASS} && -n ${ORACLE_SERVER} && -n ${DB_SERVICE} ]]; then
  export ORACLE_DRIVER_PATH=/tmp/ojdbc11-21.5.0.0.jar

  curl -sS -o "${ORACLE_DRIVER_PATH}" https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc11/21.5.0.0/ojdbc11-21.5.0.0.jar

  until java -cp "/:${ORACLE_DRIVER_PATH}" \
        /TestOracleConnection.java "jdbc:oracle:thin:${DB_USER}/${DB_PASS}@${ORACLE_SERVER}/${DB_SERVICE}"
  do
    echo $(date) " Still waiting for Oracle to start..."
    sleep 5
  done

  echo $(date) " Oracle connection successful!"
else
  echo $(date) " Skipping DB Wait"
fi

echo "----------------------------"
echo "| Step 2: Starting Wildfly |"
echo "----------------------------"

/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

until curl http://localhost:8080 -sf -o /dev/null;
do
  echo $(date) " Still waiting for Wildfly to start..."
  sleep 5
done

echo $(date) " Wildfly started!"


echo "---------------------------------"
echo "| Step 3: Execute Setup Scripts |"
echo "---------------------------------"

SETUP_DIR=/setup

for f in "${SETUP_DIR}"/*; do
       echo "Executing ${f}"
       "${f}"
done

sleep infinity