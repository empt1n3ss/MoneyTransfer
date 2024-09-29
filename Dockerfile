FROM openjdk:22
EXPOSE 5500
COPY target/Money-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

