#!/bin/bash

if [ ! -z "$1" ] && [ -f "$1" ]
then
echo "$1 exists, loading"
. $1
fi

# Verify expected env set:
while read var; do
  [ -z "${!var}" ] && { echo "$var is not set. Exiting.."; exit 1; }
done << EOF
WILDFLY_BIND_ADDRESS
WILDFLY_GROUP
WILDFLY_GROUP_ID
WILDFLY_HTTPS_PORT
WILDFLY_USER
WILDFLY_USER_HOME
WILDFLY_USER_ID
WILDFLY_VERSION
JDK_HOME
JDK_MAX_HEAP
JDK_MAX_META
EOF

WILDFLY_APP_HOME=${WILDFLY_USER_HOME}/${WILDFLY_VERSION}

remove_java_11() {
# We're assuming this leaves only JDK17
yum remove java-11-openjdk-headless -y
alternatives --auto java
}

create_user_and_group() {
groupadd -r -g ${WILDFLY_GROUP_ID} ${WILDFLY_GROUP}
useradd -r -m -u ${WILDFLY_USER_ID} -g ${WILDFLY_GROUP_ID} -d ${WILDFLY_USER_HOME} -s /bin/bash ${WILDFLY_USER}
}

download_and_unzip() {
cd /tmp
wget https://github.com/wildfly/wildfly/releases/download/${WILDFLY_VERSION}/wildfly-${WILDFLY_VERSION}.zip
unzip /tmp/wildfly-${WILDFLY_VERSION}.zip -d ${WILDFLY_USER_HOME}
mv ${WILDFLY_USER_HOME}/wildfly-${WILDFLY_VERSION} ${WILDFLY_APP_HOME}
chown -R ${WILDFLY_USER}:${WILDFLY_GROUP} ${WILDFLY_USER_HOME}
}

create_symbolic_links() {
cd ${WILDFLY_USER_HOME}
ln -s ${WILDFLY_VERSION} current
ln -s current/standalone/configuration configuration
ln -s current/standalone/log log
}

adjust_jvm_options() {
sed -i "s|#JAVA_HOME=\"/opt/java/jdk\"|JAVA_HOME=\"${JDK_HOME}\"|g" ${WILDFLY_APP_HOME}/bin/standalone.conf
sed -i "s/MaxMetaspaceSize=256m/MaxMetaspaceSize=${JDK_MAX_META}/g" ${WILDFLY_APP_HOME}/bin/standalone.conf
sed -i "s/Xmx512m/Xmx${JDK_MAX_HEAP}/g" ${WILDFLY_APP_HOME}/bin/standalone.conf
}

create_systemd_service() {
if (( ${WILDFLY_HTTPS_PORT} < 1024 ))
then
  sysctl net.ipv4.ip_unprivileged_port_start=${WILDFLY_HTTPS_PORT}
fi

cat > /etc/systemd/system/wildfly.service << EOF
[Unit]
Description=The WildFly Application Server
After=syslog.target network.target
Before=httpd.service
[Service]
EnvironmentFile=/run/wildfly.env
Environment=LAUNCH_JBOSS_IN_BACKGROUND=1
User=${WILDFLY_USER}
LimitNOFILE=102642
PIDFile=/run/wildfly.pid
ExecStart=${WILDFLY_APP_HOME}/bin/standalone.sh -b ${WILDFLY_BIND_ADDRESS} -Djboss.https.port=${WILDFLY_HTTPS_PORT}
StandardOutput=null
[Install]
WantedBy=multi-user.target
EOF
systemctl enable wildfly
systemctl start wildfly
}

create_log_file_cleanup_cron() {
cat > /root/delete-old-wildfly-logs.sh << EOF
#!/bin/sh
if [ -d ${WILDFLY_USER_HOME}/log ] ; then
 /usr/bin/find ${WILDFLY_USER_HOME}/log/ -mtime +30 -exec /usr/bin/rm {} \;
fi
EOF
chmod +x /root/delete-old-wildfly-logs.sh
cat > /etc/cron.d/delete-old-wildfly-logs.cron << EOF
0 0 * * * /root/delete-old-wildfly-logs.sh >/dev/null 2>&1
EOF
}

FUNCTIONS=(remove_java_11 create_user_and_group download_and_unzip create_symbolic_links adjust_jvm_options create_systemd_service create_log_file_cleanup_cron)

if [ ! -z "$2" ]
then
  echo "------------------------"
  echo "$2"
  echo "------------------------"
  $2
else
for i in "${!FUNCTIONS[@]}"; do
  echo "------------------------"
  echo "${FUNCTIONS[$i]}"
  echo "------------------------"
  ${FUNCTIONS[$i]};
done
fi
