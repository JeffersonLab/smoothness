ARG BUILD_IMAGE=gradle:7.3.3-jdk17

# BUILD_TYPE should be one of 'remote-src', 'local-src', 'local-artifact'
ARG BUILD_TYPE=remote-src

###
# Remote source scenario
###
FROM ${BUILD_IMAGE} as remote-src

USER root
WORKDIR /

RUN git clone https://github.com/JeffersonLab/smoothness \
   && cd smoothness \
   && gradle build -x test --no-watch-fs

###
# Local source scenario
#
# This scenario is the only one that needs .dockerignore
###
FROM ${BUILD_IMAGE} as local-src

USER root
WORKDIR /

RUN mkdir /smoothness

COPY . /smoothness

RUN cd /smoothness && gradle build -x test --no-watch-fs

###
# Local Artifact scenario
#
# If we used local-src here we'd trigger Docker cache changes before this stage/layer is reached
# and the whole point of local-artifact is to narrowly target an artifact and leverage caching
###
FROM remote-src as local-artifact

USER root
WORKDIR /

# Single out deployment artifact to leverage Docker build caching
COPY ./smoothness-demo/build/libs/smoothness-demo.war /smoothness/smoothness-demo/build/libs/

###
# Build type chooser / resolver stage
#
# The "magic" is due to Docker honoring dynamic arguments for an image to run.
#
###
FROM ${BUILD_TYPE} as builder-chooser

## Let's minimize layers in final-product by organizing files into a single copy structure
RUN mkdir -p /unicopy/opt/jboss/wildfly/standalone/configuration  \
    && mkdir -p /unicopy/opt/jboss/wildfly/modules/com/oracle/database/jdbc/main \
    && cp /smoothness/docker/wildfly/TestOracleConnection.java /unicopy \
    && cp /smoothness/docker/wildfly/docker-entrypoint.sh /unicopy \
    && cp /smoothness/run/wildfly/modules/com/oracle/database/jdbc/main/ojdbc11-21.3.0.0.jar /unicopy/opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/ojdbc11-21.3.0.0.jar \
    && cp /smoothness/docker/wildfly/modules/com/oracle/database/jdbc/main/module.xml /unicopy/opt/jboss/wildfly/modules/com/oracle/database/jdbc/main/module.xml \
    && cp /smoothness/docker/wildfly/standalone/configuration/* /unicopy/opt/jboss/wildfly/standalone/configuration

###
# Final product stage brings it all together in as small and few layers as possible.
###
FROM quay.io/wildfly/wildfly:26.0.1.Final as final-product

USER root

COPY --from=builder-chooser /unicopy /

# This must be last and separate from other copy command for caching purposes (local-artifact scenario)
COPY --from=builder-chooser /smoothness/smoothness-demo/build/libs /opt/jboss/wildfly/standalone/deployments

RUN chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

USER jboss

ENTRYPOINT ["/docker-entrypoint.sh"]