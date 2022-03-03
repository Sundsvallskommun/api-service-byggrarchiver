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
* Sokigo FB
* ByggR

## Miljövariabler
Dessa miljövariabler måste sättas för att det ska gå att köra applikationen.

FBWEBB_ORIGIN<br/>
FB_USER<br/>
FB_PASSWORD<br/>
FB_DATABASE<br/>
SUNDSVALLS_KOMMUN_INTERNAL_ORIGIN<br/>
SUNDSVALLS_KOMMUN_CONSUMER_KEY<br/>
SUNDSVALLS_KOMMUN_CONSUMER_SECRET<br/>
ARENDEEXPORT_SOAP_ORIGIN<br/>
DB_USERNAME<br/>
DB_PWD<br/>
DB_URL<br/>
DB_HIBERNATE_GENERATION<br/>

## Kör applikationen lokalt

För att köra applikationen lokalt måste du ha Docker installerat på din dator.

Använd dessa kommandon för att bygga och starta applikationen med test-config: <br/>
`docker build -f ./src/main/docker/Dockerfile -t ms-byggrarchiver:test .`<br/>
`docker-compose -f src/main/docker/docker-compose_test.yml up`


