#!/bin/bash

echo "----------------------------------"
echo "| Setup III: Config Email client |"
echo "----------------------------------"
/opt/jboss/wildfly/bin/jboss-cli.sh -c <<EOF
batch
/subsystem=mail/mail-session=jlab:add(from="wildfly@jlab.org", jndi-name="java:/mail/jlab")
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp-jlab:add(host=smtpmail.jlab.org, port=25)
/subsystem=mail/mail-session=jlab/server=smtp:add(outbound-socket-binding-ref=mail-smtp-jlab)
run-batch
EOF