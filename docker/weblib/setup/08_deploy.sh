#!/bin/bash

echo "----------------------"
echo "| Setup VIII: Deploy |"
echo "----------------------"

STAGE_DIR=/stage

cp "${STAGE_DIR}"/*.war /opt/jboss/wildfly/standalone/deployments
