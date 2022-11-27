FROM openjdk:20-ea-11-jdk

COPY target/Ecommerce-0.0.1-SNAPSHOT.jar Ecommerce-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/Ecommerce-0.0.1-SNAPSHOT.jar"]