# DDAuto

Dynamic DNS with AutoDNS

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=th-schwarz_DDAuto&metric=alert_status)](https://sonarcloud.io/dashboard?id=th-schwarz_DDAuto)
[![Build Status](https://www.travis-ci.com/th-schwarz/DDAuto.svg?token=qSPv4SNGn1yMojeF1zXi&branch=develop)](https://www.travis-ci.com/th-schwarz/DDAuto) 
![License](https://img.shields.io/github/license/th-schwarz/DDAuto)

Some measurement values

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=th-schwarz_DDAuto&metric=alert_status)](https://sonarcloud.io/dashboard?id=th-schwarz_DDAuto)  [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=th-schwarz_DDAuto&metric=security_rating)](https://sonarcloud.io/dashboard?id=th-schwarz_DDAuto)  [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=th-schwarz_DDAuto&metric=coverage)](https://sonarcloud.io/dashboard?id=th-schwarz_DDAuto)  [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=th-schwarz_DDAuto&metric=ncloc)](https://sonarcloud.io/dashboard?id=th-schwarz_DDAuto)  

Give it a try!   [![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/th-schwarz/DDAuto?include_prereleases)](https://github.com/th-schwarz/DDAuto/releases)

DDAuto is a simple REST-service for dynamic DNS. It uses the [Java Domainrobot Sdk](https://github.com/InterNetX/java-domainrobot-sdk) to update the IPv4 and IPv6 addresses of a host and can be used with many routers, for example the AVM Fritz!Box.
DDAuto can be executed by `commandline`, `init.d` or `systemd`.

Requirement: An AutoDNS account! For further details have a look at [AutoDNS: Efficient Domain-Management for Resellers](https://www.internetx.com/en/domains/autodns)

If you find a bug or you certain features, don't hesitate to file a ticket.


## Big picture

DDAuto is running as a service. A client can access the service via url and basic-auth. Let's assume DDAuto is running on `localhost`, then we can update the IPv4 and IPv6 address of the host _mydyndns.domain.com_ by calling the following curl command:
```bash
curl -u dyndns:test123 -i \ 
   "http://localhost:8081/update/mydyndns.domain.com/?apitoken=1234567890abcdf&ipv4=127.1.2.4&ipv6=2a03:4000:41:32::2"
```
For each host an api-token can be defined. If the api-token doesn't match the host, the update will be rejected. Because of this security feature, DDAuto can be used by different people. They can't update the IPs each other.


## Configuration and start

DDAuto is a spring boot application, that's why the application is extremely customizable just by configuration! Details of the configuration can be found here: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#application-properties and https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#actuator.monitoring.customizing-management-server-port

Side note: To avoid the use of the java keystore tool, DDAuto could be run behind a proxy. Corresponding headers are set by default.

Here is the suggested directory/file structure:

```bash
├── /opt/ddauto
│   ├── application.yml
│   ├── ddauto-0.1.0-SNAPSHOT.jar
│   ├── ddauto.jar -> ddauto-0.1.0-SNAPSHOT.jar
│   ├── ddauto-zone.log   (log of the zone updates)
│   ├── log4j2.xml
```
application.yml defines the properties. An example can be found [here](https://github.com/th-schwarz/DDAuto/blob/develop/src/main/resources/application.yml)<br>
Important: The basic-auth, the api-tokens and the credentials for AutoDNS should be defined in this file!

The start by command line looks like:
```bash
java -jar /opt/ddauto/ddauto.jar \
   --spring.config.location=file:///opt/ddauto/application.yml
```
DDAuto can also be started easily as a service. For further information, see https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#deployment.installing.nix-services


## Basic routes

All routes are secured by basic-auth. A detailed description of the routes can be found here: https://th-schwarz.github.io/DDAuto/


## Suggested AutoDNS setup

TODO


## Setup a router for dynamic DNS

As an example, let's have a look at the setup of dynamic DNS in the [Fritz!Box 7590](https://service.avm.de/help/en/FRITZ-Box-7530/019p2/hilfe_dyndns). 
The following settngs are required:
* *DynDNS Provider:* User-defined
* *Domain name:* The hostname for which the IPs should be updated.
* *Username / Password:* The credentials for basic-auth.
* *Update-URL:* {baseUrl}/update/\<domain\>/?apitoken=[yourApitoken]&ipv4=\<ipaddr\>&ipv6=\<ip6addr\> <br>
  If both IP parameters are omitted, an attempt is made to fetch the remote IP.
