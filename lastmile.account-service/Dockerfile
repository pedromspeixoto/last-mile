# Dockerize Spring Application
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY target/*.jar /app/app.jar
# Copy NewRelic Agent
COPY newrelic/* /app/
ENTRYPOINT ["java", "-javaagent:newrelic.jar" ,"-jar", "app.jar"]