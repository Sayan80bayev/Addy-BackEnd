FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ./target/demo-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]