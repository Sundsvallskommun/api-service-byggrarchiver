# ByggR Archiver


## Leverantör
Sundsvalls Kommun

## Beskrivning
ByggR Archiver används för att arkivera dokument från ByggR.

## Integrationer
Tjänsten har integrationer mot följande API:er:

* Archive
* Messaging
* FB (Sokigo)
* ÄrendeExport/ByggR (Sokigo) 

## Tekniska detaljer

### Konfiguration

|Miljövariabel|Beskrivning|
|---|---|
|**Inställningar för utgående e-post**||
|`email.lantmateriet.sender`|Avsändande e-postadress för e-post relaterad till Lantmäteriet|
|`email.lantmateriet.recipient`|Mottagande e-postadress för e-post till Lantmäteriet|
|`email.extension-error.sender`|Avsändande e-postadress för e-post rörande felaktiga filtyper|
|`email.extension-error.recipient`|Mottagande e-postadress för e-post rörande felaktiga filtyper|
|**Inställningar för långtidsarkiv**||
|`long-term-archive.url`|URL till långtidsarkiv|
|**Inställningar för schemaläggning**||
|`cron.expression`|Cron-jobb för schemalagd arkivering|
|**Databasinställningar**||
|`spring.datasource.driver-class-name`|JDBC-driver-klass för anslutning till databas|
|`spring.datasource.url`|JDBC-URL för anslutning till databas|
|`spring.datasource.username`|Användarnamn till databas|
|`spring.datasource.password`|Lösenord till databas|
|**Inställningar för archive-integration**||
|`integration.archive.url`|API-URL|
|`integration.archive.oauth2.token-url`|URL för att hämta OAuth2-token|
|`integration.archive.oauth2.client-id`|OAuth2-klient-id|
|`integration.archive.oauth2.client-secret`|OAuth2-klient-nyckel|
|**Inställningar för messaging-integration**||
|`integration.messaging.url`|API-URL|
|`integration.messaging.oauth2.token-url`|URL för att hämta OAuth2-token|
|`integration.messaging.oauth2.client-id`|OAuth2-klient-id|
|`integration.messaging.oauth2.client-secret`|OAuth2-klient-nyckel|
|**Inställningar för Sokigo FB-integration**||
|`integration.fb.url`|API-URL|
|`integration.fb.username`|Användarnamn|
|`integration.fb.password`|Lösenord|
|`integration.fb.database`|Databas|
|**Inställningar för Sokigo Ärendeexport/ByggR-integration**||
|`integration.arendeexport.url`|URL|

## Status
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)


Copyright &copy; 2022 Sundsvalls Kommun
