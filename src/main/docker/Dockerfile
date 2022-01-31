FROM maven:3.8.4-jdk-11 AS build

WORKDIR /code
COPY settings.xml /root/.m2/settings.xml

COPY pom.xml pom.xml
RUN mvn dependency:go-offline --batch-mode

COPY . .
ARG PROFILE=""
RUN mvn clean package $PROFILE

FROM openjdk:11.0.12
ENV TZ="Europe/Stockholm"

COPY --from=build /code/target/*runner.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080