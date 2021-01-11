# smoothness [![Java CI with Gradle](https://github.com/JeffersonLab/smoothness/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JeffersonLab/smoothness/actions?query=workflow%3A%22Java+CI+with+Gradle%22) [![Bintray](https://img.shields.io/bintray/v/slominskir/maven/smoothness-weblib?label=Download)](https://bintray.com/slominskir/maven/smoothness-weblib)
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application template and [JSP tag library](https://docs.oracle.com/javaee/5/tutorial/doc/bnama.html) based on the [JQuery UI Smoothness](https://jqueryui.com/themeroller/) theme.  The included demo web application showcases the template layout.

![Screenshot](https://github.com/JeffersonLab/smoothness/raw/main/smoothness-demo/Screenshot.png?raw=true "Screenshot")

---
 - [Build](https://github.com/JeffersonLab/smoothness#build)
 - [Configure](https://github.com/JeffersonLab/smoothness#configure)
 - [Publish](https://github.com/JeffersonLab/smoothness#publish)
---

## Build
This [Java 11](https://adoptopenjdk.net/) project uses the [Gradle 5](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

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
| PROXY_HOSTNAME | Name of outermost proxy host (for use in hyperlinks in generated emails and log entries) |
| CDN_HOSTNAME | Name of content delivery network host for shared resources |
| KEYCLOAK_HOSTNAME | Name of Keycloak authentication server |
| KEYCLOAK_CLIENT_ID_(context) | Each application context requires a unique Keycloak client ID |
| LOGBOOK_HOSTNAME | Name of Jefferson Lab logbook server |
| LOGBOOK_OPS_BOOKS_CSV | Comma separated list of logbook names |
| SERVER_MESSAGE | Banner message (optional) - useful to tag test environment |


### Database
The demo application requires an Oracle 18 database with the following [schema](https://github.com/JeffersonLab/smoothness/tree/main/smoothness-demo/schema) installed.   The application server hosting the demo app must also be configured with a JNDI datasource.

## Publish

You can publish new versions of this library to Bintray.  First ensure you have the bintray token configured in your [user gradle properties](https://gist.github.com/slominskir/dff89309ecdc424f134fdf02ceb41906).  Then use the Gradle task bintrayUpload followed by bintrayPublish.
