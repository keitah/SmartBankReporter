FROM gradle:8.9-jdk21 AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle --no-daemon clean installDist

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/project/build/install/SmartBankReporter/ /app/
ENV DB_URL=jdbc:postgresql://host.docker.internal:5432/smartbank         DB_USER=reporter         DB_PASSWORD=reporter
ENTRYPOINT ["/app/bin/SmartBankReporter"]
