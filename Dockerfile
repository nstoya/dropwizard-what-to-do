FROM maven:3.6-jdk-11 AS MAVEN_BUILD
 
# copy the pom and src code to the container
COPY / /
 
# package application code
RUN mvn clean package
 
# the second stage of our build will use open jdk 8 on alpine 3.9
FROM openjdk:16-alpine
 
# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/dropwizard-what-to-do-1.0-SNAPSHOT.jar /dropwizard-what-to-do.jar
COPY ./config-prod.yml /config-prod.yml
 
# set the startup command to execute the jar
CMD ["java", "-jar", "/dropwizard-what-to-do.jar" , "server", "/config-prod.yml"]