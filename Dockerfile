FROM gradle:8.8 AS build
COPY --chown=gradle:gradle . /home/pspdemo/src
WORKDIR /home/pspdemo/src
RUN gradle build --debug

FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build /home/pspdemo/src/build/libs/psp-demo-0.0.1-SNAPSHOT.jar /app/
RUN bash -c 'touch /app/psp-demo-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-jar","/app/psp-demo-0.0.1-SNAPSHOT.jar"]
