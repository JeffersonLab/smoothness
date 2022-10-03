# smoothness [![CI](https://github.com/JeffersonLab/smoothness/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/smoothness/actions/workflows/ci.yml) [![Docker (weblib)](https://img.shields.io/docker/v/slominskir/smoothness-weblib?sort=semver&label=DockerHub+weblib)](https://hub.docker.com/r/slominskir/smoothness-weblib) [![Docker (demo)](https://img.shields.io/docker/v/slominskir/smoothness-demo?sort=semver&label=DockerHub+demo)](https://hub.docker.com/r/slominskir/smoothness-demo) [![Maven Central](https://badgen.net/maven/v/maven-central/org.jlab/smoothness-weblib)](https://repo1.maven.org/maven2/org/jlab/smoothness-weblib/) 
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application template and [JSP tag library](https://docs.oracle.com/javaee/5/tutorial/doc/bnama.html) based on the [JQuery UI Smoothness](https://jqueryui.com/themeroller/) theme.  The included demo web application showcases the template layout.

![Screenshot](https://github.com/JeffersonLab/smoothness/raw/main/smoothness-demo/Screenshot.png?raw=true "Screenshot")

---
 - [Overview](https://github.com/JeffersonLab/smoothness#overview)
 - [Quick Start with Compose](https://github.com/JeffersonLab/smoothness#quick-start-with-compose) 
 - [Install](https://github.com/JeffersonLab/smoothness#install)
 - [API](https://github.com/JeffersonLab/smoothness#api)  
 - [Configure](https://github.com/JeffersonLab/smoothness#configure)
 - [Build](https://github.com/JeffersonLab/smoothness#build)
 - [Release](https://github.com/JeffersonLab/smoothness#release)
 - [See Also](https://github.com/JeffersonLab/smoothness#see-also)
---

## Overview
The template is designed for database-centric web applications in the JLab accelerator environment and supports interactions with the following dependent services (for the demo they're Containers):
 - [Keycloak](https://github.com/keycloak/keycloak) - OAuth OIDC authentication
 - [Oracle DB](https://github.com/gvenzl/oci-oracle-xe) - data persistence (test server linked)
 - [Puppet-Show](https://github.com/slominskir/puppet-show) - HTML-to-PDF reports
 - [SMTP Email](https://github.com/mailhog/MailHog) - send email programmatically (test server linked)
 - [JLab Logbook](https://github.com/JeffersonLab/elog) - operations electronic event logging

Client-side libraries (JavaScript):
 - [jQuery](https://jquery.com/) - General Web API wrapper
 - [jQuery UI](https://jqueryui.com/) - General User Interface widgets
 - [select2](https://select2.org/) - Improved select widget (multi-select)
 - [Flot](https://www.flotcharts.org/) - Graphing and reports

Server-side librarires (Java):
 - [Tuckey urlrewrite](https://tuckey.org/urlrewrite/) - URL Rewriting (cache busting resource versioning)
 - [Hibernate](https://hibernate.org/) - Object-Relational Mapping (persistence).
 - [JLog](https://github.com/JeffersonLab/jlog) - JLab elog API

This git repo is actually comprised of two projects tied together in a [Gradle Multi-Project build](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html): 

  1. **smoothness-weblib** - the library for sharing
  1. **smoothness-demo** - a demo application that uses the library

The demo is included in a Gradle Multi-Project build mainly so we can take advantage of the [artifact dependency feature](https://docs.gradle.org/current/userguide/declaring_dependencies_between_subprojects.html), such that we can build the library and quickly test it in the demo without having to publish to a maven artifact repo and then download it from the repo (faster build/deploy/test cycle).

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/smoothness
cd smoothness
```
2. Launch Docker
```
docker compose up
```
3. Navigate to page ([Link](http://localhost:8080/smoothness-demo))
```
http://localhost:8080/smoothness-demo
```

**Note**: Login with demo username "tbrown" and password "password". 

**See**: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
### Web Lib Install
This library requires a Java 11+ JVM and standard library at run time, plus a Java EE 8+ application server (developed with Wildfly). 

You can obtain the library jar file from the [Maven Central repository](https://repo1.maven.org/maven2/org/jlab/) directly or from a Maven friendly build tool with the following coordinates (Gradle example shown):
```
implementation 'org.jlab:smoothness-weblib:<version>'
```
Check the [Release Notes](https://github.com/JeffersonLab/smoothness/releases) to see what has changed in each version. 

### Demo Install
This application requires a Java 11+ JVM and standard library to run, plus a Java EE 8+ application server (developed with Wildfly).  Use the Docker Compose quickstart to automate the setup of the app, else manually:

   1. Download [Wildfly 26.1.2](https://www.wildfly.org/downloads/) (or just pull it out of the Docker image config and all - see below)
   1. Download [demo.war](https://github.com/JeffersonLab/smoothness-demo/releases) and deploy it to Wildfly
   1. Configure Wildfly<sup>Note</sup> and start it
   1. Navigate your web browser to localhost:8080/smoothness-demo

**Note**: The docker image configures Wildfly for use in the compose environment and that's a good starting point to copy from.  Outside of a compose environment you may need to tweak the standalone.xml configuration to use different host names and ports (For example Oracle and Keycloak host names would need to be updated to localhost:1521 and localhost:8081 respectively when using the deps.yml and running Wildfly outside the compose network):

```
docker compose up
docker exec -it demo /opt/jboss/wildfly/bin/jboss-cli.sh --connect -c "undeploy smoothness-demo.war"
docker exec -it demo /opt/jboss/wildfly/bin/jboss-cli.sh --connect -c shutdown
docker cp demo:/opt/jboss/wildfly .
```

As an alternative, you can create a `.env` file for your environment and call the bash scripts `env-setup-server.sh` and `env-setup-app.sh` to do the initial Wildfly configuration.   Bash can be executed on Linux, Windows, and Mac with some perseverance.  See [bash setup scripts](https://github.com/JeffersonLab/smoothness/tree/main/bash).

**Note**: The application requires an Oracle 21+ database with the following [schema](https://github.com/JeffersonLab/smoothness/tree/main/docker/oracle/setup) installed.

## API
[javadocs and tlddocs](https://jeffersonlab.github.io/smoothness/)

## Configure

### Environment Variables

#### Configtime
Wildfly must be pre-configured before the first deployment of the app.  The scripts located in the `bash` directory are used with the following environment variables:

<b>Server Setup (once):</b>

| Name                | Description                                                  |
|---------------------|--------------------------------------------------------------|
| EMAIL_FROM          | Default from address for the mail/jlab resource              |
| EMAIL_HOST          | Host for the mail/jlab resource                              |
| EMAIL_PORT          | Port for the mail/jlab resource                              |
| ORACLE_DRIVER_PATH  | Path to ORACLE Driver (see Dockerfile-weblib for docker env) |
| WILDFLY_HOME        | Path to Wildfly home dir                                     | 


<b>App Setup (1 or more):</b>

| Name                | Description                                                              |
|---------------------|--------------------------------------------------------------------------|
| KEYCLOAK_REALM      | Keycloak realm to configure                                              |
| KEYCLOAK_RESOURCE   | Keycloak resource to configure                                           |
| KEYCLOAK_SECRET     | Keycloak Secret                                                          |
| KEYCLOAK_SERVER_URL | Protocol, host name, and port of Keycloak authentication server          |
| KEYCLOAK_WAR        | Name of war file to secure with Keycloak (app key)                       |
| ORACLE_DATASOURCE   | Name of Oracle datasource (app key)                                      |
| ORACLE_SERVER       | Host name and port of Oracle server to use to connect to DB from Wildfly |
| ORACLE_SERVICE      | Oracle Service name to use to connect to DB from Wildfly                 |
| ORACLE_USER         | Username to use to connect to DB from Wildfly                            |
| ORACLE_PASS         | Password to use to connect to DB from Wildfly                            |
| WILDFLY_HOME        | Path to Wildfly home dir                                                 | 

#### Global Runtime
At runtime smoothness apps use the following global environment variables:

| Name                     | Description                                                                                                                                                                                                                                                                                                                        |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CDN_SERVER               | Optional (Unless RESOURCE_LOCATION=CDN). Host name and port of content delivery network host for shared smoothness resources                                                                                                                                                                                                       |
| CDN_VERSION              | Optional (Unless RESOURCE_LOCATION=CDN). Version of smoothness lib on CDN                                                                                                                                                                                                                                                          |
| JLAB_RUN_URL             | Optional - URL to JLab run dates lookup JSON service (used by date picker)                                                                                                                                                                                                                                                         |
| KEYCLOAK_SU_IDP          | Optional - If specified a Switch User (SU) link will appear next to login with the [kc_idp_hint](https://www.keycloak.org/docs/latest/server_admin/#_client_suggested_idp).  Useful when default login uses [SPNEGO/Kerberos](https://en.wikipedia.org/wiki/SPNEGO) or perhaps [WebAuthn](https://en.wikipedia.org/wiki/WebAuthn). |
| KEYCLOAK_HEADLESS_IDP    | Optional - A Keycloak IDP can only have one SPNEGO attempt, so if you need an additional one it can be done in the background on click of "login" link by defining this additional IDP here                                                                                                                                        |
| KEYCLOAK_SERVER_FRONTEND | Optional (Unless SU_IDP or HEADLESS_IDP) - Host name and port of public Keycloak (may differ from confidential host:port in proxy situations (often Docker with localhost)                                                                                                                                                         |                                                                                                                                     
| KEYCLOAK_REALM           | Keycloak realm name to use with background IDP and SU and also with user authorization cache service                                                                                                                                                                                                                               |
| KEYCLOAK_RESOURCE        | Keycloak resource to use with user authorization cache service                                                                                                                                                                                                                                                                     |
| KEYCLOAK_SECRET          | Keycloak secret to use with user authorization cache service                                                                                                                                                                                                                                                                       |
| KEYCLOAK_SERVER_URL      | Keycloak protocol, host name, and port to use with the user authorization cache service                                                                                                                                                                                                                                            |
| LOGBOOK_SERVER           | Host name and port of Jefferson Lab logbook server                                                                                                                                                                                                                                                                                 |
| ORACLE_SERVER            | Docker Only - Host name and port of Oracle server to use to connect to DB from TestOracleConnection utility                                                                                                                                                                                                                        |
| ORACLE_USER              | Docker Only - Username to use to connect to DB from TestOracleConnection utility                                                                                                                                                                                                                                                   |
| ORACLE_PASS              | Docker Only - Password to use to connect to DB from TestOracleConnection utility                                                                                                                                                                                                                                                   |
| ORACLE_SERVICE           | Docker Only - Oracle Service name to use to connect to DB from TestOracleConnection utility                                                                                                                                                                                                                                        |
| PROXY_SERVER             | Host name and port of outermost proxy host (for use in hyperlinks in generated emails and log entries)                                                                                                                                                                                                                             |                                                                                                                                                                                                                                                                                                           |
| PUPPET_SHOW_SERVER       | Host name and port of Puppet Show server                                                                                                                                                                                                                                                                                           |
| RESOURCE_LOCATION        | Optional - If undefined then defaults to LOCAL (serve files locally).  Other option is CDN, which looks for minified/combined files on shared Content Delivery Network (CDN) server - Nice for when multiple apps use same resources to have warm cache.                                                                           |
| SERVER_MESSAGE           | Optional - Banner message at top of all pages - useful to tag test environment or provide global announcement                                                                                                                                                                                                                      |

#### Per App Runtime
At runtime smoothness apps use the following per-app namespaced environment variables (prefix set in web.xml context init param):

| Name                             | Description                                                                                                        |
|----------------------------------|--------------------------------------------------------------------------------------------------------------------|
| (PREFIX)_FEEDBACK_SENDER_ADDRESS | Email address to set as the sender (separate from the "from" field) when a feedback message is submitted by a user |                                                                                                                                     
| (PREFIX)_FEEDBACK_TO_ADDRESS_CSV | Comma Separated Values of email addresses to notify when a feedback message is submitted by a user                 |
| (PREFIX)_CONTENT_CONTACT         | Optional - Person to contact for help with app content                                                             |                                                                                                                                     
| (PREFIX)_TECHNICAL_CONTACT       | Optional - Person to contact for help with app not working as expected                                             |

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/smoothness
cd smoothness
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

**Note**: The dependency jars (except Java EE 8 jars required to be available in the server) are included in the war file that is generated by the build by default, but you can optionally exclude them (if you intend to install them into the application server) with the flag -Pprovided like so:
```
gradlew -Pprovided build
```

**See**: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Release
Since this is a monorepo there are actually two projects: the weblib and the demo of the weblib.  Often a new version of the weblib is tagged and released first then then the sister demo version is released, conventionally by creating a new release tag with the same version but with the `-demo` suffix.

0. Bump the version number and release date in settings.gradle and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).

**WEBLIB**
1. Create a new release on the GitHub [Releases](https://github.com/JeffersonLab/jaws-effective-processor/releases) page corresponding to same version in settings.gradle (Enumerate changes and link issues).  This is for the weblib.
2. Build and push [Docker image](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#8-build-an-image-based-of-github-tag).  For the weblib so use `-f Dockerfile-weblib`.
3. Publish new artifact on maven central with:
```
gradlew publishMavenPublicationToOSSRHRepository
```
**Note**: You then must navigate to https://s01.oss.sonatype.org/ and manually click around to close and release.  There is a plugin for Gradle that automates this, but it [doesn't work](https://github.com/gradle-nexus/publish-plugin/issues/81) with multi-project builds.

4. Update javadocs and tlddocs by copying them from build dir into gh-pages branch and updating index.html (commit, push).

**DEMO**    
1. Update Dockerfile-demo to use the new weblib image
2. Create a new release with the same version number as just used above, but with `-demo` and attach the war file so users can easily install.
3. Build and push Docker image for the demo `-f Dockerfile-demo`.
4. Bump and commit quick start [image version](https://github.com/JeffersonLab/smoothness/blob/main/docker-compose.override.yml).  For the demo.

## See Also
- [Beam authorization manager (BAM)](https://github.com/JeffersonLab/bam)
- [Down time manager (DTM)](https://github.com/JeffersonLab/dtm)
