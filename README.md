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

### Bygga och starta tjänsten med Docker

Bygg en Docker-image av tjänsten:

```
mvn spring-boot:build-image
```

Starta en Docker-container:

```
docker run -i --rm -p 8080:8080 evil.sundsvall.se/ms-byggrarchiver:latest
```

Copyright &copy; 2022 Sundsvalls Kommun
