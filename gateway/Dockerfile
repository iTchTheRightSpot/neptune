FROM maven:3.9.9-amazoncorretto-23 as build

WORKDIR /

COPY pom.xml pom.xml
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY ./src src/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:23

WORKDIR /

COPY --from=build /target/gateway.jar /

CMD ["java", "-jar", "/gateway.jar"]