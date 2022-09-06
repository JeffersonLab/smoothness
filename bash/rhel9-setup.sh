#!/bin/bash

# Verify expected env set:
while read var; do
  [ -z "${!var}" ] && { echo "$var is not set. Exiting.."; exit 1; }
done << EOF
WILDFLY_VERSION
WILDFLY_USER
WILDFLY_GROUP
WILDFLY_USER_ID
WILDFLY_GROUP_ID
WILDFLY_HOME
JVM_MAX_META
JVM_MAX_HEAP
EOF

remove_java_11() {
# We're assuming this leaves only JDK17
yum remove java-11-openjdk-headless -y
}

create_user_and_group() {
groupadd -r -g ${WILDFLY_GROUP_ID} ${WILDFLY_GROUP}
useradd -r -m -u ${WILDFLY_USER_ID} -g ${WILDFLY_GROUP_ID} -d ${WILDFLY_HOME} -s /bin/bash ${WILDFLY_USER}
}

download_and_unzip() {
cd /tmp
wget https://github.com/wildfly/wildfly/releases/download/${WILDFLY_VERSION}/wildfly-${WILDFLY_VERSION}.zip
unzip /tmp/wildfly-${WILDFLY_VERSION}.zip -d ${WILDFLY_HOME}
mv ${WILDFLY_HOME}/wildfly-${WILDFLY_VERSION} ${WILDFLY_HOME}/${WILDFLY_VERSION}
chown -R ${WILDFLY_USER}:${WILDFLY_GROUP} ${WILDFLY_HOME}
}

create_symbolic_links() {
cd ${WILDFLY_HOME}
ln -s ${WILDFLY_VERSION} pro
ln -s pro/standalone/configuration configuration
ln -s pro/standalone/log log
}

adjust_jvm_options() {
sed -i "s/MaxMetaspaceSize=256m/MaxMetaspaceSize=${JVM_MAX_META}/g" ${WILDFLY_HOME}/pro/bin/standalone.conf
sed -i "s/Xmx512m/Xmx${JVM_MAX_HEAP}/g" ${WILDFLY_HOME}/pro/bin/standalone.conf
}

create_systemd_service() {
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
ExecStart=${WILDFLY_HOME}/${WILDFLY_VERSION}/bin/standalone.sh
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
if [ -d ${WILDFLY_HOME}/log ] ; then
 /usr/bin/find ${WILDFLY_HOME}/log/ -mtime +30 -exec /usr/bin/rm {} \;
fi
EOF
chmod +x /root/delete-old-wildfly-logs.sh
cat > /etc/cron.d/delete-old-wildfly-logs.cron << EOF
0 0 * * * /root/delete-old-wildfly-logs.sh >/dev/null 2>&1
EOF
}

echo "--------------------------"
echo "| Setup I: Remove JDK 11 |"
echo "--------------------------"
remove_java_11

echo "-----------------------------------"
echo "| Setup II: Create user and group |"
echo "-----------------------------------"
create_user_and_group

echo "-----------------------------------------"
echo "| Setup III: Download and unzip Wildfly |"
echo "-----------------------------------------"
download_and_unzip

echo "-----------------------------------"
echo "| Setup IV: Create symbolic links |"
echo "-----------------------------------"
create_symbolic_links

echo "-------------------------------"
echo "| Setup V: Adjust JVM Options |"
echo "-------------------------------"
adjust_jvm_options

echo "------------------------------------"
echo "| Setup VI: Create systemd service |"
echo "------------------------------------"
create_systemd_service

echo "-------------------------------------------"
echo "| Setup VII: Create log file cleanup cron |"
echo "-------------------------------------------"
create_log_file_cleanup_cron
