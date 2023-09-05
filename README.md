# ByggrArchiver

https://sundsvall.atlassian.net/wiki/spaces/SK/pages/718274575/ByggrArchiver

## Config

### Production-config

- **API Gateway:**                  api-i.sundsvall.se
- **Endpoint:**                     Production
- **Server:**                       microservices.sundsvall.se
- **DB:**                           Maria DB
- **Integrations:**                 Production

### Test-config

- **API Gateway:**                  api-i-test.sundsvall.se
- **Endpoint:**                     Production
- **Server:**                       microservices-test.sundsvall.se
- **DB:**                           Maria DB
- **Integrations:**                 Test

## Integrations
This application has integrations against these API's:
* Archive
* Messaging
* FB (Sokigo)
* ByggR (Sokigo)

## Run app locally
`mvn clean spring-boot:run -Dspring.profiles.active=local`

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)

## 
Copyright (c) 2021 Sundsvalls kommun
