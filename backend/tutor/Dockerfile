FROM azul/zulu-openjdk-alpine:11
COPY pom.xml ./pom.xml
COPY src ./src
COPY target/tutor-service-1.0-SNAPSHOT.jar ./
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./tutor-service-1.0-SNAPSHOT.jar"]