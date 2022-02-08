FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/demo-1.0-SNAPSHOT.jar demo-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","demo-1.0-SNAPSHOT.jar"]
