# DDAuto

Dynamic DNS with AutoDNS

DDAuto is a simple REST-service for dynamic DNS. It uses the [Java Domainrobot Sdk](https://github.com/InterNetX/java-domainrobot-sdk) to update the IPv4 and IPv6 addresses of a host and can be used with many routers, for example the AVM Fritz!Box.
DDAuto can be executed by `commandline`, `init.d` or `systemd`.

Requirement: An AutoDNS account! For further details have a look at [AutoDNS: Efficient Domain-Management for Resellers](https://www.internetx.com/en/domains/autodns)

[![Build Status](https://www.travis-ci.com/th-schwarz/DDAuto.svg?token=qSPv4SNGn1yMojeF1zXi&branch=develop)](https://www.travis-ci.com/th-schwarz/DDAuto)

## Big picture

DDAuto is running as a service. A client can access the service via url and basic-auth. Let's assume DDAuto is running on `localhost`, then we can update the IPv4 and IPv6 address of the host _mydyndns.domain.com_ by calling the following curl command:
```bash
curl -u dyndns:test123 -i  "http://localhost:8081/update/mydyndns.domain.com/?apitoken=1234567890abcdf&ipv4=127.1.2.4&ipv6=2a03:4000:41:32::2"
```
The parameter _ipv6_ is optional.
For each hos	t an api-token can be defined. If the api-token doesn't match the host, the update will be rejected. Because of this security feature, DDAuto can be used by different people. They can't update the IPs each other.

## Configuration and start

DDAuto is a spring boot application, that's why the application is extremely customizable only by configuration! Details can be found here: https://docs.spring.io/spring-boot/docs/2.5.2/reference/htmlsingle/#application-properties

Here is the suggested directory structure:

```bash
├── /opt/ddauto
│   ├── api-token.properties
│   ├── application.properties
│   ├── ddauto-0.1.0-SNAPSHOT.jar
│   ├── ddauto.jar -> ddauto-0.1.0-SNAPSHOT.jar
│   ├── ddauto-zone.log
│   ├── log4j2.xml
│   ├── zone.properties
```
