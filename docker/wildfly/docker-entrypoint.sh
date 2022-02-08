#!/bin/bash


if [[ -z ${SKIP_DB_WAIT} ]]; then
  echo "-------------------------------------------------"
  echo "Step 1: Waiting for Oracle DB to start listening "
  echo "-------------------------------------------------"

  until java -cp /:/opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/ojdbc11-21.3.0.0.jar \
        /TestOracleConnection.java "jdbc:oracle:thin:${DB_USER}/${DB_PASS}@${ORACLE_SERVER}/${DB_SERVICE}"
  do
    echo -e $(date) " Still waiting for Oracle to start..."
    sleep 5
  done

  echo -e $(date) " Oracle connection successful!"
else
  echo "Skipping DB Wait"
fi

/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0