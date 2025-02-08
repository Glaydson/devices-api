FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/devicesapi-0.0.1-SNAPSHOT.jar devices-api.jar
ENTRYPOINT ["java","-jar","/devices-api.jar"]