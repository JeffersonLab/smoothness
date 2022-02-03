# smoothness [![Java CI with Gradle](https://github.com/JeffersonLab/smoothness/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JeffersonLab/smoothness/actions?query=workflow%3A%22Java+CI+with+Gradle%22) [![Maven Central](https://badgen.net/maven/v/maven-central/org.jlab/smoothness-weblib)](https://repo1.maven.org/maven2/org/jlab/smoothness-weblib/)
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application template and [JSP tag library](https://docs.oracle.com/javaee/5/tutorial/doc/bnama.html) based on the [JQuery UI Smoothness](https://jqueryui.com/themeroller/) theme.  The included demo web application showcases the template layout.

![Screenshot](https://github.com/JeffersonLab/smoothness/raw/main/smoothness-demo/Screenshot.png?raw=true "Screenshot")

---
 - [Overview](https://github.com/JeffersonLab/smoothness#overview)
 - [Usage](https://github.com/JeffersonLab/smoothness#usage)
 - [Build](https://github.com/JeffersonLab/smoothness#build)
 - [Configure](https://github.com/JeffersonLab/smoothness#configure)
 - [Demo App](https://github.com/JeffersonLab/smoothness#demo-app)
 - [Database](https://github.com/JeffersonLab/smoothness#database)
 - [See Also](https://github.com/JeffersonLab/smoothness#see-also)
---

## Overview
The template is designed for database-centric web applications in the JLab accelerator environment and therefore supports interactions with the following dependent services:
 - [Keycloak](https://github.com/keycloak/keycloak) - OAuth OIDC authentication
 - [Oracle DB](https://github.com/gvenzl/oci-oracle-xe) - data persistence (test server linked)
 - [Puppet-Show](https://github.com/slominskir/puppet-show) - HTML-to-PDF reports
 - [SMTP Email](https://github.com/mailhog/MailHog) - send email programmatically (test server linked)
 - [JLab Logbook](https://github.com/JeffersonLab/elog) - operations electronic event logging

## Usage
The library is a jar file plus dependencies on Java EE and the Java 11+ JVM and standard library.  You can obtain the smoothness-weblib jar file from the [Maven Central repository](https://repo1.maven.org/maven2/org/jlab/) directly or from a Maven friendly build tool with the following coordinates (Gradle example shown):
```
implementation 'org.jlab:smoothness-weblib:<version>'
```
You can check the [Release Notes](https://github.com/JeffersonLab/smoothness/releases) to see what has changed in each version. 

## Build
This [Java 11](https://adoptopenjdk.net/) project uses the [Gradle 6](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/smoothness
cd smoothness
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

## Configure

### Environment Variables
| Name | Description |
|---|---|
| SMOOTH_PROXY_HOSTNAME | Name of outermost proxy host (for use in hyperlinks in generated emails and log entries) |
| SMOOTH_RESOURCE_LOCATION | If undefined then defaults to LOCAL (server files locally).  Other option is CDN, which looks for minified/combined files on shared server. |
| SMOOTH_CDN_HOSTNAME | Name of content delivery network host for shared smoothness resources (Only if SMOOTH_RESOURCE_LOCATION = CDN |
| SMOOTH_CDN_SMOOTHNESS_VERSION | Version of smoothness lib on CDN (only if SMOOTH_RESOURCE_LOCATION = CDN) |
| SMOOTH_KEYCLOAK_HOSTNAME | Name of Keycloak authentication server |
| SMOOTH_KEYCLOAK_CLIENT_ID_(context) | Each application context requires a unique Keycloak client ID |
| SMOOTH_LOGBOOK_HOSTNAME | Name of Jefferson Lab logbook server |
| SMOOTH_LOGBOOK_OPS_BOOKS_CSV | Comma separated list of logbook names |
| SMOOTH_SERVER_MESSAGE | Banner message (optional) - useful to tag test environment |


### Demo App
This git repo is actually comprised of two projects tied together in a [Gradle Multi-Project build](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html): 

  1. **smoothness-weblib** - the library for sharing
  1. **smoothness-demo** - a demo application that uses the library

The demo is included in a Gradle Multi-Project build mainly so we can take advantage of the [artifact dependency feature](https://docs.gradle.org/current/userguide/declaring_dependencies_between_subprojects.html), such that we can build the library and quickly test it in the demo without having to publish to a maven artifact repo and then download it from the repo (faster build/deploy/test cycle).

### Database
The demo application requires an Oracle 18+ database with the following [schema](https://github.com/JeffersonLab/smoothness/tree/main/smoothness-demo/schema) installed.   The application server hosting the demo app must also be configured with a JNDI datasource.   See [Oracle XE DB Container Notes](https://github.com/JeffersonLab/smoothness/wiki/Developer-Notes#oracle-container).

## See Also
- [Down time manager (DTM)](https://github.com/JeffersonLab/dtm)
- [Beam authorization manager (BAM)](https://github.com/JeffersonLab/bam)
