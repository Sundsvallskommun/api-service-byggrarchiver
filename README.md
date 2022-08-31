# ByggrArchiver

https://sundsvall.atlassian.net/wiki/spaces/SK/pages/718274575/ByggrArchiver

## Config

### Production-config

- **API Gateway:**                  api-i.sundsvall.se
    - **Endpoint:**                 Production
- **Server:**                       microservices.sundsvall.se
- **DB:**                           Maria DB
- **Version av integrationer:**     Production

### Test-config

- **API Gateway:**                  api-i-test.sundsvall.se
    - **Endpoint:**                 Production
- **Server:**                       microservices-test.sundsvall.se
- **DB:**                           Maria DB
- **Version av integrationer:**     Test

### Sandbox-config

- **API Gateway:**                  api-i-test.sundsvall.se
    - **Endpoint:**                 Sandbox
- **Server:**                       microservices-test.sundsvall.se
- **DB:**                           H2 (in-memory)
- **Version av integrationer:**     Mocked (Wiremock)

## Integrationer
Denna applikation har direkta integrationer mot:
* Archive
* Messaging
* FB (Sokigo)
* ByggR (Sokigo)

## Kör applikationen lokalt
Använd detta kommandon för att köra applikationen lokalt: <br/>
`mvn clean spring-boot:run -Dspring.profiles.active=local`


