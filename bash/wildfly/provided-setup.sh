#!/bin/bash

FUNCTIONS=(config_provided)

VARIABLES=(PROVIDED_LIBS
           WILDFLY_APP_HOME)

if [[ $# -eq 0 ]] ; then
    echo "Usage: $0 [var file] <optional function>"
    echo "The var file arg should be the path to a file with bash variables that will be sourced."
    echo "The optional function name arg if provided is the sole function to call, else all functions are invoked sequentially."
    printf 'Variables: '
    printf '%s ' "${VARIABLES[@]}"
    printf '\n'
    printf 'Functions: '
    printf '%s ' "${FUNCTIONS[@]}"
    printf '\n'
    exit 0
fi

if [ ! -z "$1" ] && [ -f "$1" ]
then
echo "Loading environment $1"
. $1
fi

# Verify expected env set:
for i in "${!VARIABLES[@]}"; do
  var=${VARIABLES[$i]}
  [ -z "${!var}" ] && { echo "$var is not set. Exiting."; exit 1; }
done

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
for LINE in $(echo "${PROVIDED_LIBS}")
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

