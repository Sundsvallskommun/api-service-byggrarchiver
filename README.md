# ByggR Archiver

_The service orchestrates the process of sending and archiving documents from Bygg-R in the long-term archive (LTA)._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **MariaDB**
- **Maven**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/Sundsvallskommun/api-byggrarchiver.git
cd api-service-byggrarchiver
```

2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

   ```bash
   mvn spring-boot:run
   ```

   - Using Gradle:

   ```bash
   gradle bootRun
   ```

## Dependencies

This microservice depends on the following services:

- **Archive**
  - **Purpose:** Used for sending documents to the long-term storage archive (LTA).
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-archive](https://github.com/Sundsvallskommun/api-service-archive)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.
- **Messaging**
  - **Purpose:** Used for sending execution reports and information emails.
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-messaging](https://github.com/Sundsvallskommun/api-service-messaging)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.
- **Arendeexport**
  - **Purpose:** Used for retreiving documents from ByggR that is to be evaluated and possibly sent to the long-term storage archive.
  - **Repository:** External service managed by Sokigo company
- **FB webb**
  - **Purpose:** Used for retreiving property information
  - **Repository:** External service managed by Sokigo company

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Usage

### API Endpoints

See the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/2281/batch-jobs
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

```yaml
server:
  port: 8080
```

- **External Service URLs**

```yaml
scheduler:
  municipality-ids: <comma separated string of municipality ids to process>
  cron:
    expression: <cron expression for execution interval>

email:
  extension-error:
    recipient: <email for recipient of error information mail>
    sender: <email for sender of error information mail>
  lantmateriet:
    recipient: <email for recipient of status information mail for geo errands>
    sender: <email for sender of status information mail for geo errands>
  status:
    recipient: <email for recipient of status information mail>
    sender: <email for sender of status information mail>

integration:
  archive:
    maximum-file-size: <file size threshold determining if file will be processed or not>
    oauth2:
      clientId: <client-id>
      clientSecret: <client-secret>
      tokenUrl: <token-url>
    url: <service-url>
  arendeexport:
    connectTimeout: <connect-timeout in seconds>
    readTimeout: <read-timeout in seconds>
    url: <service-url>
  fb:
    database: <database-name>
    username: <username>
    password: <password>
    url: <service-url>
  messaging:
    oauth2:
      clientId: <client-id>
      clientSecret: <client-secret>
      tokenUrl: <token-url>
    url: <service-url>
long-term-archive:
  url: <service-url>
spring:
  datasource:
    url: jdbc:mysql://<server-id>:<port>/<database-name>
    username: <db-username>
    password: <db-password>
```

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
config:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-byggrarchiver&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-byggrarchiver)

---

&copy; 2022 Sundsvalls kommun
