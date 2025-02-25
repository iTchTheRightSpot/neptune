FROM eclipse-temurin:23 as build

WORKDIR /

COPY pom.xml pom.xml
COPY .mvn/ .mvn/
COPY ./src src/

RUN mvn clean install -DskipTests

FROM eclipse-temurin:23

WORKDIR /

COPY --from=build /target/inventory.jar /inventory.jar

CMD ["java", "-jar", "/inventory.jar"]