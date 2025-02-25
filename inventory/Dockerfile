FROM eclipse-temurin:23 as build

WORKDIR /

COPY pom.xml pom.xml
COPY .mvn/ .mvn/
COPY ./src src/

RUN mvn clean install -DskipTests

FROM eclipse-temurin:23

WORKDIR /

COPY --from=build /target/inventory-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]