# http://whitfin.io/speeding-up-maven-docker-builds/

FROM maven:3-jdk-11
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src


# Skip tests
RUN mvn -Dmaven.test.skip=true package
EXPOSE 8080

# Set to production mode
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod", "-jar", "./target/tutor-0.0.1-SNAPSHOT.jar"]

