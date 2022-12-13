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

### Sandbox-config

- **API Gateway:**                  api-i-test.sundsvall.se
- **Endpoint:**                     Sandbox
- **Server:**                       microservices-test.sundsvall.se
- **DB:**                           Maria DB
- **Integrations:**                 Mocked (Wiremock)

## Integrations
This application has integrations against these API's:
* Archive
* Messaging
* FB (Sokigo)
* ByggR (Sokigo)

## Run app locally
`mvn clean spring-boot:run -Dspring.profiles.active=local`


