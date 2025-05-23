FROM gradle:8.5.0-jdk21 AS build
WORKDIR /app
RUN git clone https://github.com/joacoseo1/sigema.git .

RUN gradle clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]