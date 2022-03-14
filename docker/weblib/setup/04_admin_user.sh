#!/bin/bash

echo "----------------------------"
echo "| Setup IV: Add Admin User |"
echo "----------------------------"

config_admin_user() {
if [[ -z "${WILDFLY_USER}" ]]; then
    echo "Skipping Wildfly admin user Setup: Must provide WILDFLY_USER in environment"
    return 0
fi

if [[ -z "${WILDFLY_PASSWORD}" ]]; then
    echo "Skipping Wildfly admin user Setup: Must provide WILDFLY_PASSWORD in environment"
    return 0
fi

/opt/jboss/wildfly/bin/add-user.sh "${WILDFLY_USER}" "${WILDFLY_PASSWORD}"
}

config_admin_user