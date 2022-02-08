ARG BUILD_IMAGE=gradle:7.3.3-jdk11

ARG CUSTOM_CRT_URL

# BUILD_TYPE should be one of 'remote-src', 'local-src', 'local-artifcat'
ARG BUILD_TYPE=remote-src

FROM ${BUILD_IMAGE} as remote-src

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

FROM ${BUILD_IMAGE} as local-src

USER root
WORKDIR /

RUN mkdir /smoothness

COPY . /smoothness/

RUN cd /smoothness && gradle build -x test

# If we used local-src here we'd trigger Docker cache changes before this stage/layer is reached
# and the whole point of local-artifact is to narrowly target an artifact and leverage caching
FROM remote-src as local-artifact

USER root
WORKDIR /

# Single out deployment artifact to leverage Docker build caching
COPY ./smoothness-demo/build/libs/smoothness-demo.war /smoothness/smoothness-demo/build/libs/

# The "magic" is due to Docker honoring dynamic arguments for an image
FROM ${BUILD_TYPE} as builder-chooser

FROM quay.io/wildfly/wildfly:26.0.1.Final as final-product

USER root

COPY --from=builder-chooser /smoothness/docker/wildfly/TestOracleConnection.java /TestOracleConnection.java
COPY --from=builder-chooser /smoothness/docker/wildfly/docker-entrypoint.sh /docker-entrypoint.sh
COPY --from=builder-chooser /smoothness/docker/wildfly/modules/com/oracle/database/jdbc/main/ojdbc11-21.3.0.0.jar /opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/ojdbc11-21.3.0.0.jar
COPY --from=builder-chooser /smoothness/docker/wildfly/modules/com/oracle/database/jdbc/main/module.xml /opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/module.xml
COPY --from=builder-chooser /smoothness/docker/wildfly/standalone/configuration /opt/jboss/wildfly/standalone/configuration
COPY --from=builder-chooser /smoothness/smoothness-demo/build/libs /opt/jboss/wildfly/standalone/deployments

RUN chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

USER jboss

ENTRYPOINT ["/docker-entrypoint.sh"]