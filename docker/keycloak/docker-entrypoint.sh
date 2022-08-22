#!/bin/bash

echo "------------------------"
echo "| Step 1: Import realm |"
echo "------------------------"
/opt/keycloak/bin/kc.sh import --dir /data

echo "--------------------------"
echo "| Step 2: Start Keycloak |"
echo "--------------------------"

/opt/keycloak/bin/kc.sh start-dev &

sleep infinity