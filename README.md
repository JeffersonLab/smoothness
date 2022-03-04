# smoothness [![Java CI with Gradle](https://github.com/JeffersonLab/smoothness/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JeffersonLab/smoothness/actions?query=workflow%3A%22Java+CI+with+Gradle%22) [![Docker](https://img.shields.io/docker/v/slominskir/smoothness?sort=semver&label=DockerHub)](https://hub.docker.com/r/slominskir/smoothness) [![Maven Central](https://badgen.net/maven/v/maven-central/org.jlab/smoothness-weblib)](https://repo1.maven.org/maven2/org/jlab/smoothness-weblib/) 
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application template and [JSP tag library](https://docs.oracle.com/javaee/5/tutorial/doc/bnama.html) based on the [JQuery UI Smoothness](https://jqueryui.com/themeroller/) theme.  The included demo web application showcases the template layout.

![Screenshot](https://github.com/JeffersonLab/smoothness/raw/main/smoothness-demo/Screenshot.png?raw=true "Screenshot")

---
 - [Overview](https://github.com/JeffersonLab/smoothness#overview)
 - [Quick Start with Compose](https://github.com/JeffersonLab/smoothness#quick-start-with-compose) 
 - [Install](https://github.com/JeffersonLab/smoothness#install)
 - [API](https://github.com/JeffersonLab/smoothness#api)  
 - [Configure](https://github.com/JeffersonLab/smoothness#configure)
 - [Build](https://github.com/JeffersonLab/smoothness#build)
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

**See**: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install

This library requires a Java 11+ JVM and standard library at run time, plus a Java EE 8+ application server (developed with Wildfly). 

You can obtain the library jar file from the [Maven Central repository](https://repo1.maven.org/maven2/org/jlab/) directly or from a Maven friendly build tool with the following coordinates (Gradle example shown):
```
implementation 'org.jlab:smoothness-weblib:<version>'
```
Check the [Release Notes](https://github.com/JeffersonLab/smoothness/releases) to see what has changed in each version. 

### Demo Install
Use the Docker Compose quickstart to automate the setup of the demo, else manually:

 - Run the [Gradle build](https://github.com/JeffersonLab/smoothness#build) to generate "run" directory
 - Download [Wildfly 26](https://www.wildfly.org/downloads/)
 - Configure Wildfly as seen in the [Docker example](https://github.com/JeffersonLab/smoothness/tree/main/docker/wildfly).  To simply use the docker wildfly config, but with a local instance of Wildfly (instead of Wildfly in a container) set the following environment variables on the host:
   - **KEYCLOAK_SERVER**: localhost:8081
   - **ORACLE_SERVER**: localhost:1521
   - **JBOSS_MODULEPATH**: `<absolute-path-to-wildfly>`\modules;`<absolute-path-to-project>`\run\wildfly\modules
   - **JAVA_OPTS**: -Djboss.server.base.dir=`<absolute-path-to-project>`\run\wildfly\standalone
 - Start Wildfly 
 - Copy smoothness-demo.war into the Wildfly deployments directory
 - Navigate your web browser to localhost:8080/smoothness-demo

**Note**: Windows path separators shown.  For Linux replace the semicolon with a colon and the back slash with forward slash.

**Note**: The demo application requires an Oracle 21+ database with the following [schema](https://github.com/JeffersonLab/smoothness/tree/main/docker/oracle/setup) installed.   The application server hosting the demo app must also be configured with a JNDI datasource.   See [Oracle XE DB Container Notes](https://github.com/JeffersonLab/smoothness/wiki/Developer-Notes#oracle-container).

## API
[javadocs and tlddocs](https://jeffersonlab.github.io/smoothness/)

## Configure

### Environment Variables

| Name                     | Description                                                                                                                                                                                                                                                                                                                        |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| PROXY_SERVER             | Host name and port of outermost proxy host (for use in hyperlinks in generated emails and log entries)                                                                                                                                                                                                                             |                                                                                                                                                                                                                                                                                                           |
| RESOURCE_LOCATION        | Optional - If undefined then defaults to LOCAL (serve files locally).  Other option is CDN, which looks for minified/combined files on shared Content Delivery Network (CDN) server - Nice for when multiple apps use same resources to have warm cache.                                                                           |
| CDN_SERVER               | Optional (Unless RESOURCE_LOCATION=CDN). Host name and port of content delivery network host for shared smoothness resources                                                                                                                                                                                                       |
| CDN_VERSION              | Optional (Unless RESOURCE_LOCATION=CDN). Version of smoothness lib on CDN                                                                                                                                                                                                                                                          |
| KEYCLOAK_SERVER          | Host name and port of Keycloak authentication server                                                                                                                                                                                                                                                                               |
| KEYCLOAK_SU_IDP          | Optional - If specified a Switch User (SU) link will appear next to login with the [kc_idp_hint](https://www.keycloak.org/docs/latest/server_admin/#_client_suggested_idp).  Useful when default login uses [SPNEGO/Kerberos](https://en.wikipedia.org/wiki/SPNEGO) or perhaps [WebAuthn](https://en.wikipedia.org/wiki/WebAuthn). |
| KEYCLOAK_HEADLESS_IDP    | Optional - A Keycloak IDP can only have one SPNEGO attempt, so if you need an additional one it can be done in the background on click of "login" link by defining this additional IDP here                                                                                                                                        |
| KEYCLOAK_SERVER_FRONTEND | Optional (Unless SU_IDP or HEADLESS_IDP) - Host name and port of public Keycloak (may differ from confidential host:port in proxy situations (often Docker with localhost)                                                                                                                                                         |                                                                                                                                     
| KEYCLOAK_REALM           | Optional (Unless SU_IDP or HEADLESS_IDP) - Realm name to use with background IDP and SU                                                                                                                                                                                                                                            |
| PUPPET_SHOW_SERVER       | Host name and port of Puppet Show server                                                                                                                                                                                                                                                                                           |
| LOGBOOK_SERVER           | Host name and port of Jefferson Lab logbook server                                                                                                                                                                                                                                                                                 |
| SERVER_MESSAGE           | Optional - Banner message at top of all pages - useful to tag test environment or provide global announcement                                                                                                                                                                                                                      |
| ORACLE_SERVER            | Host name and port of Oracle server                                                                                                                                                                                                                                                                                                |
| DB_USER                  | Username to use to connect to DB from app and from TestOracleConnection utility                                                                                                                                                                                                                                                    |
| DB_PASS                  | Password to use to connect to DB from app and from TestOracleConnection utility                                                                                                                                                                                                                                                    |
| DB_SERVICE               | Oracle Service name to use to connect to DB from app and from TestOracleConnection utility                                                                                                                                                                                                                                         |

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

## See Also
- [Beam authorization manager (BAM)](https://github.com/JeffersonLab/bam)
- [Down time manager (DTM)](https://github.com/JeffersonLab/dtm)
