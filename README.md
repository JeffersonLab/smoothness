# smoothness [![CI](https://github.com/JeffersonLab/smoothness/actions/workflows/ci.yaml/badge.svg)](https://github.com/JeffersonLab/smoothness/actions/workflows/ci.yaml) [![Docker (demo)](https://img.shields.io/docker/v/jeffersonlab/smoothness-demo?sort=semver&label=DockerHub+demo)](https://hub.docker.com/r/jeffersonlab/smoothness-demo) [![Maven Central](https://badgen.net/maven/v/maven-central/org.jlab/smoothness-weblib)](https://repo1.maven.org/maven2/org/jlab/smoothness-weblib/) 
A [Java EE 10](https://en.wikipedia.org/wiki/Jakarta_EE) web application library consisting of both a Java class library and a [JSP tag library](https://docs.oracle.com/javaee/5/tutorial/doc/bnama.html) based on the [JQuery UI Smoothness](https://jqueryui.com/themeroller/) theme. A related project [smoothness-template](https://github.com/JeffersonLab/smoothness-template) provides a repo template that uses the library.  This repo includes a demo web application to showcase the library and template.

![Screenshot](https://github.com/JeffersonLab/smoothness/raw/main/smoothness-demo/Screenshot.png?raw=true "Screenshot")

---
 - [Overview](https://github.com/JeffersonLab/smoothness#overview)
 - [Quick Start with Compose](https://github.com/JeffersonLab/smoothness#quick-start-with-compose) 
 - [Install](https://github.com/JeffersonLab/smoothness#install)
 - [API](https://github.com/JeffersonLab/smoothness#api)  
 - [Configure](https://github.com/JeffersonLab/smoothness#configure)
 - [Build](https://github.com/JeffersonLab/smoothness#build)
 - [Develop](https://github.com/JeffersonLab/smoothness#develop) 
 - [Release](https://github.com/JeffersonLab/smoothness#release)
 - [Deploy](https://github.com/JeffersonLab/smoothness#deploy)
 - [See Also](https://github.com/JeffersonLab/smoothness#see-also)   
---

## Overview
The template is designed for database-centric web applications in the JLab accelerator environment and supports interactions with the following dependent services (for the demo they're Containers):
 - [Keycloak](https://github.com/keycloak/keycloak) - OAuth OIDC authentication
 - [Oracle DB](https://github.com/gvenzl/oci-oracle-xe) - data persistence (test server linked)
 - [Puppet-Show](https://github.com/slominskir/puppet-show) - HTML-to-PDF reports
 - [SMTP Email](https://github.com/mailhog/MailHog) - send email programmatically (test server linked)
 - [JLab Logbook](https://github.com/JeffersonLab/elog) - operations electronic event logging

[Version Info](https://github.com/JeffersonLab/smoothness/blob/main/deps.yaml)

Client-side libraries (JavaScript):
 - [jQuery](https://jquery.com/) - General Web API wrapper
 - [jQuery UI](https://jqueryui.com/) - General User Interface widgets
 - [select2](https://select2.org/) - Improved select widget (multi-select)
 - [Flot](https://www.flotcharts.org/) - Graphing and reports

[Version info](https://github.com/JeffersonLab/smoothness/tree/main/smoothness-weblib/src/main/resources/META-INF/resources/resources)

Server-side librarires (Java):
 - [Tuckey urlrewrite](https://tuckey.org/urlrewrite/) - URL Rewriting (cache busting resource versioning)
 - [Hibernate](https://hibernate.org/) - Object-Relational Mapping (persistence).
 - [JLog](https://github.com/JeffersonLab/jlog) - JLab elog API

[Version Info](https://github.com/JeffersonLab/smoothness/blob/485f9cee249a2e897e8d61081342023754312ddb/smoothness-demo/build.gradle#L21-L31)

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
2. Launch [Compose](https://github.com/docker/compose)
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
This library requires a Java 17+ JVM and standard library at run time, plus a Java EE 10 application server (developed with Wildfly). 

You can obtain the library jar file from the [Maven Central repository](https://repo1.maven.org/maven2/org/jlab/) directly or from a Maven friendly build tool with the following coordinates (Gradle example shown):
```
implementation 'org.jlab:smoothness-weblib:<version>'
```
Check the [Release Notes](https://github.com/JeffersonLab/smoothness/releases) to see what has changed in each version. 

### Demo Install
This application requires a Java 17+ JVM and standard library to run, plus a Java EE 10 application server (developed with Wildfly).  Use the Docker Compose quickstart to automate the setup of the app, else manually:

1. Install service [dependencies](https://github.com/JeffersonLab/smoothness/blob/main/deps.yaml)
2. Download [Wildfly 37.0.1](https://www.wildfly.org/downloads/)
3. [Configure](https://github.com/JeffersonLab/smoothness#configure) Wildfly and start it
4. Download [demo.war](https://github.com/JeffersonLab/smoothness-demo/releases) and deploy it to Wildfly
5. Navigate your web browser to [localhost:8080/smoothness-demo](http://localhost:8080/smoothness-demo)

## API
[javadocs and tlddocs](https://jeffersonlab.github.io/smoothness/)

## Configure

### Environment Variables

#### Configtime
We use the [JeffersonLab/wildfly](https://github.com/JeffersonLab/wildfly) bash scripts to setup Wildfly.  See [JeffersonLab/wildfly#configure](https://github.com/JeffersonLab/wildfly#configure).

#### Global Runtime
At runtime smoothness apps use the following global environment variables:

| Name                         | Description                                                                                                                                                                                                                                                                                                                        |
|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| BACKEND_SERVER_URL           | Scheme, host name, and port of app server from inside the backend network.   This only differs from FRONTEND_SERVER_URL in some scenarios, such as Docker.  Needed for puppet show service.                                                                                                                                        |
| JLAB_RUN_URL                 | Optional - URL to JLab run dates lookup JSON service (used by date picker)                                                                                                                                                                                                                                                         |
| KEYCLOAK_SU_IDP              | Optional - If specified a Switch User (SU) link will appear next to login with the [kc_idp_hint](https://www.keycloak.org/docs/latest/server_admin/#_client_suggested_idp).  Useful when default login uses [SPNEGO/Kerberos](https://en.wikipedia.org/wiki/SPNEGO) or perhaps [WebAuthn](https://en.wikipedia.org/wiki/WebAuthn). |
| KEYCLOAK_HEADLESS_IDP        | Optional - A Keycloak IDP can only have one SPNEGO attempt, so if you need an additional one it can be done in the background on click of "login" link by defining this additional IDP here                                                                                                                                        |
| KEYCLOAK_BACKEND_SERVER_URL  | Scheme, host name, and port of Keycloak used by the user authorization cache service (may differ from KEYCLOAK_FRONTEND_SERVER_URL in proxy situations or with Docker)                                                                                                                                                             |
| KEYCLOAK_FRONTEND_SERVER_URL | Optional (Unless SU_IDP or HEADLESS_IDP) - Scheme, host name, and port of public Keycloak (may differ from confidential host:port in proxy situations (often Docker with localhost)                                                                                                                                                |                                                                                                                                     
| KEYCLOAK_REALM               | Keycloak realm name to use with background IDP and SU and also with user authorization cache service                                                                                                                                                                                                                               |
| KEYCLOAK_RESOURCE            | Keycloak resource to use with user authorization cache service                                                                                                                                                                                                                                                                     |
| KEYCLOAK_SECRET              | Keycloak secret to use with user authorization cache service                                                                                                                                                                                                                                                                       |
| LOGBOOK_SERVER_URL           | Scheme, host name and port of Jefferson Lab logbook server                                                                                                                                                                                                                                                                         |
| FRONTEND_SERVER_URL          | Scheme, host name, and port of outermost proxy host (for use in hyperlinks in generated emails and log entries)                                                                                                                                                                                                                    |                                                                                                                                                                                                                                                                                                           
| PUPPET_SHOW_SERVER_URL       | Scheme, host name, and port of Puppet Show server used by the html-to-image and html-to-pdf app relative path Convert service                                                                                                                                                                                                      |
| SERVER_MESSAGE               | Optional - Banner message at top of all pages - useful to tag test environment or provide global announcement                                                                                                                                                                                                                      |

### Per App Runtime Settings
There are some [Settings](https://github.com/JeffersonLab/smoothness/blob/main/container/oracle/initdb.d/04_settings.sql) in the database that can be edited on the Setup tab by admins.                                                                           

## Build
This project is built with [Java 21](https://adoptium.net/) (compiled to Java 17 bytecode), and uses the [Gradle 9](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/smoothness
cd smoothness
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

**Note**: The dependency jars are expected to be installed into Wildfly directly to keep the application war size small and reuse components.  See [provided-setup.sh](https://github.com/JeffersonLab/smoothness/blob/main/bash/wildfly/provided-setup.sh) and [docker-server.env](https://github.com/JeffersonLab/smoothness/blob/main/docker/weblib/docker-server.env).

**See**: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Develop
In order to iterate rapidly when making changes it's often useful to run the app directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the service dependencies with:
```
docker compose -f deps.yaml up
```
**Note**: The local install of Wildfly should be [configured](https://github.com/JeffersonLab/smoothness#configure) to proxy connections to services via localhost and therefore the environment variables should contain:
```
KEYCLOAK_BACKEND_SERVER_URL=http://localhost:8081
FRONTEND_SERVER_URL=https://localhost:8443
```
Further, the local DataSource must also leverage localhost port forwarding so the `standalone.xml` connection-url field should be: `jdbc:oracle:thin:@//localhost:1521/xepdb1`.  

The [server](https://github.com/JeffersonLab/wildfly/blob/main/scripts/server-setup.sh) and [app](https://github.com/JeffersonLab/wildfly/blob/main/scripts/app-setup.sh) setup scripts can be used to setup a local instance of Wildfly. 

## Release
Since this is a monorepo there are actually two projects: the weblib and the demo of the weblib.  Further, both projects have a Java distributable (demo war and weblib jar), plus there is a demo Docker image.

Dependencies (libraries) generally should be installed directly into Wildfly as opposed to being bundled inside a war file, but the smoothness weblib itself should not and must be packaged inside the war file of each app using the lib.  This is necessary as the smoothness weblib is [incompatible as a JBoss Module](https://github.com/JeffersonLab/smoothness/issues/4), plus this bundling makes development of the lib easier as it allows iteration without constantly updating weblib code installed in Wildfly.   Since the demo has a dependency on the weblib, either all artifacts needed for both subprojects need to tagged together, else two separate releases would be needed as a release corresponds to a git tag and Docker images are built using the Git tag context.  We use a shared release.

1. During development the build is run locally to ensure everything is working.   You can use deps.yaml Docker Compose in concert with a local Wildfly instance to quickly iterate.
2. To confirm the new demo Docker image is good, run the docker build and test locally:
```
docker compose -f build.yaml build demo --no-cache --progress=plain
...
docker compose -f build.yaml up
```
3. Bump the version number in the VERSION file and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
4. The [CD](https://github.com/JeffersonLab/smoothness/blob/main/.github/workflows/cd.yaml) GitHub Action should run automatically invoking:
    - The [Create release](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-release.yaml) GitHub Action to tag the source and create release notes summarizing any pull requests.   Edit the release notes to add any missing details.  A war file artifact is attached to the release.
    - The [Publish artifact](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/maven-publish.yaml) GitHub Action to create a deployment artifact on maven central.
    - The [Publish docs](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-pages-publish.yaml) GitHub Action to create javadocs.
    - The [Publish docker image](https://github.com/JeffersonLab/container-workflows/blob/main/.github/workflows/docker-publish.yaml) GitHub Action to create a new demo Docker image.
    - The [Deploy to JLab](https://github.com/JeffersonLab/general-workflows/blob/main/.github/workflows/jlab-deploy-app.yaml) GitHub Action to deploy to the JLab test environment.
5. Copy updated minified JS and CSS to any CDN as needed.

## Deploy
At JLab this app is found at [ace.jlab.org/smoothness-demo](https://ace.jlab.org/smoothness-demo) and internally at [acctest.acc.jlab.org/smoothness-demo](https://acctest.acc.jlab.org/smoothness-demo).  However, those servers are proxies for `wildfly5.acc.jlab.org` and `wildflytest5.acc.jlab.org` respectively.   A [deploy script](https://github.com/JeffersonLab/wildfly/blob/main/scripts/deploy.sh) is provided to automate wget and deploy.  Example:

```
/root/setup/deploy.sh smoothness-demo v1.2.3
```

**JLab Internal Docs**:  [InstallGuideWildflyRHEL9](https://accwiki.acc.jlab.org/do/view/SysAdmin/InstallGuideWildflyRHEL9)

## See Also
- [JLab ACE smoothness list](https://github.com/search?q=org%3Ajeffersonlab+topic%3Aace+topic%3Asmoothness&type=repositories)
