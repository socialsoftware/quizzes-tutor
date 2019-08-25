FROM openjdk:11-jre-slim
VOLUME /tmp
EXPOSE 8080
RUN mkdir -p /spring-api/
RUN mkdir -p /spring-api/logs/
COPY target/*.jar /spring-api/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod", "-jar", "/spring-api/app.jar"]