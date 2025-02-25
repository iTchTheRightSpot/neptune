FROM eclipse-temurin:23 as build

WORKDIR /

COPY pom.xml pom.xml
COPY .mvn/ .mvn/
COPY ./src src/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:23

WORKDIR /

COPY --from=build /target/gateway-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]