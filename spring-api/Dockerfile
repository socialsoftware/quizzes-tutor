# http://whitfin.io/speeding-up-maven-docker-builds/

FROM maven:3-jdk-11 as maven
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn -Dmaven.test.skip=true package



# https://stackoverflow.com/questions/53375613/why-is-the-java-11-base-docker-image-so-large-openjdk11-jre-slim

FROM azul/zulu-openjdk-alpine:11
COPY --from=maven target/tutor-0.0.1-SNAPSHOT.jar ./
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod", "-jar", "./tutor-0.0.1-SNAPSHOT.jar"]

