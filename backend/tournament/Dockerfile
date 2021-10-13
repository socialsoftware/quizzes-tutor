FROM azul/zulu-openjdk-alpine:11
COPY pom.xml ./pom.xml
COPY src ./src
COPY target/tournament-service-1.0-SNAPSHOT.jar ./
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar", "./tournament-service-1.0-SNAPSHOT.jar"]