# ---- build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cached separately from the source copy below so `mvn dependency:go-offline` only reruns when
# pom.xml actually changes, not on every source edit.
COPY pom.xml ./
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Matches <finalName>subscriptionrights</finalName> in pom.xml.
COPY --from=build /app/target/subscriptionrights.jar ./app.jar

# Informational only -- the actual host<->container mapping is set by docker-compose.yml's
# `ports:` entry, driven by SUBSCRIPTIONS_SERVER_PORT (see application.yml, which the app itself
# reads from that same env var at startup).
EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
