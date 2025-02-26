FROM maven:3.9.9-amazoncorretto-23 as build

WORKDIR /

COPY pom.xml pom.xml
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY ./src src/

RUN chmod +x mvnw

RUN mvn clean package -DskipTests

FROM eclipse-temurin:23

WORKDIR /

COPY --from=build /target/inventory.jar /

CMD ["java", "-jar", "/inventory.jar"]