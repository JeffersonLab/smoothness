#!/bin/bash

echo "----------------------------"
echo "| Setup VII: Reload Config |"
echo "----------------------------"

/opt/jboss/wildfly/bin/jboss-cli.sh -c --command=:reload