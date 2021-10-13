FROM azul/zulu-openjdk-alpine:11
COPY pom.xml ./pom.xml
COPY src ./src
COPY target/apigateway-1.0-SNAPSHOT.jar ./
ENTRYPOINT ["java", "-jar", "./apigateway-1.0-SNAPSHOT.jar"]