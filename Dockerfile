FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /target/flight-0.0.1-SNAPSHOT.jar /app/flight.jar
EXPOSE 8080
CMD ["java", "-jar", "flight.jar"]  