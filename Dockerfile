FROM gradle:6.6.1-jdk11 as builder

ARG CUSTOM_CRT_URL

USER root
WORKDIR /

RUN git clone https://github.com/JeffersonLab/smoothness \
   && cd smoothness \
   && if [ -z "$CUSTOM_CRT_URL" ] ; then echo "No custom cert needed"; else \
        wget -O /usr/local/share/ca-certificates/customcert.crt $CUSTOM_CRT_URL \
      && update-ca-certificates \
      && keytool -import -alias custom -file /usr/local/share/ca-certificates/customcert.crt -cacerts -storepass changeit -noprompt \
      && export OPTIONAL_CERT_ARG=-Djavax.net.ssl.trustStore=$JAVA_HOME/lib/security/cacerts \
        ; fi \
   && gradle build -x test $OPTIONAL_CERT_ARG

FROM quay.io/wildfly/wildfly:26.0.1.Final

USER root

COPY --from=builder /smoothness/smoothness-demo/build/libs /opt/jboss/wildfly/standalone/deployments
COPY --from=builder /smoothness/smoothness-demo/build/ojdbc11-21.3.0.0.jar /opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/ojdbc11-21.3.0.0.jar
COPY --from=builder /smoothness/docker/wildfly/TestOracleConnection.java /TestOracleConnection.java
COPY --from=builder /smoothness/docker/wildfly/docker-entrypoint.sh /docker-entrypoint.sh
COPY --from=builder /smoothness/docker/wildfly/module.xml /opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/module.xml
COPY --from=builder /smoothness/docker/wildfly/configuration /opt/jboss/wildfly/standalone/configuration

RUN chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

USER jboss

ENTRYPOINT ["/docker-entrypoint.sh"]