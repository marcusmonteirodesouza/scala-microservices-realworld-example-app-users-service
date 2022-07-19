FROM mozilla/sbt:11.0.13_1.6.2 as build

WORKDIR /app

COPY . .

RUN sbt assembly

FROM gcr.io/distroless/java11-debian11

WORKDIR /app

COPY --from=build /app/target/scala-2.13/scala-microservices-realworld-example-app-users_service-service-assembly-0.1.0-SNAPSHOT.jar .

CMD ["scala-microservices-realworld-example-app-users_service-service-assembly-0.1.0-SNAPSHOT.jar"]