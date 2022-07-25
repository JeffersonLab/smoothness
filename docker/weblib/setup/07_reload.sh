#!/bin/bash

echo "----------------------------"
echo "| Setup VII: Reload Config |"
echo "----------------------------"

if [[ -z "${SKIP_RELOAD_AFTER_CONFIG}" ]]; then
  /opt/jboss/wildfly/bin/jboss-cli.sh -c --command=:reload
fi