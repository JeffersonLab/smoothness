#!/bin/bash

if [ -z "$1" ]
  then
    echo "No file path provided"
    exit 1
fi

if [ ! -f "$1" ]; then
    echo "$1 does not exist."
    exit 1
fi

# Verify expected env set:
while read var; do
  [ -z "${!var}" ] && { echo "$var is not set. Exiting.."; exit 1; }
done << EOF
WILDFLY_APP_HOME
EOF

WILDFLY_CLI_PATH=${WILDFLY_APP_HOME}/bin/jboss-cli.sh

config_provided_dep() {

cd /tmp

LOCAL_RESOURCES=()
IFS=","
read -a resources <<<"${RESOURCES_CSV}"
for x in "${resources[@]}" ;do
    #echo "> [$x]"
    wget -nv ${x}
    LOCAL_RESOURCES+=(/tmp/`basename "${x}"`)
done

IFS=","
LOCAL_RESOURCES_CSV=`echo "${LOCAL_RESOURCES[*]}"`

${WILDFLY_CLI_PATH} -c <<EOF
batch
module add --name=${DEP_NAME} --resource-delimiter=, --resources=${LOCAL_RESOURCES_CSV} --dependencies=${DEPENDENCIES_CSV}
/subsystem=ee/:list-add(name=global-modules,value={"name"=>"${DEP_NAME}","slot"=>"main"})
run-batch
EOF
}

IFS=$'\n'
for LINE in $(cat $1)
do
  echo "${LINE}"
  IFS="|"
  read -a fields <<<"${LINE}"
  DEP_NAME=${fields[0]}
  RESOURCES_CSV=${fields[1]}
  DEPENDENCIES_CSV=${fields[2]}

  echo "DEP_NAME: ${DEP_NAME}"
  echo "RESOURCES_CSV: ${RESOURCES_CSV}"
  echo "DEPENDENCIES_CSV: ${DEPENDENCIES_CSV}"

  config_provided_dep
done
unset IFS

