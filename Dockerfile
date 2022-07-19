FROM eclipse-temurin:11 as build

# Install sbt
RUN apt-get update
RUN apt-get install apt-transport-https curl gnupg -yqq
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update
RUN apt-get install -y sbt

WORKDIR /app

COPY . .

RUN sbt assembly

FROM gcr.io/distroless/java11-debian11

WORKDIR /app

COPY --from=build /app/target/scala-2.13/scala-microservices-realworld-example-app-users_service-service-assembly-0.1.0-SNAPSHOT.jar .

CMD ["scala-microservices-realworld-example-app-users_service-service-assembly-0.1.0-SNAPSHOT.jar"]