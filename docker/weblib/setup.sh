#!/bin/bash

echo "----------------------------"
echo "| Step 1: Starting Wildfly |"
echo "----------------------------"

/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

until curl http://localhost:8080 -sf -o /dev/null;
do
  echo $(date) " Still waiting for Wildfly to start..."
  sleep 5
done

echo $(date) " Wildfly started!"


echo "---------------------------------"
echo "| Step 2: Execute Setup Scripts |"
echo "---------------------------------"

SETUP_DIR=/setup

for f in "${SETUP_DIR}"/*; do
       echo "Executing ${f}"
       "${f}"
done


echo "----------------------------"
echo "| Step 3: Shutdown Wildfly |"
echo "----------------------------"

/opt/jboss/wildfly/bin/jboss-cli.sh --connect -c shutdown