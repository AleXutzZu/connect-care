FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy pom.xml and download dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project source and build
COPY . .
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install openssl for the key generation script
RUN apt-get update && apt-get install -y openssl

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy and prepare the key generation script
COPY scripts/generate_keys.sh /app/generate_keys.sh
RUN chmod +x /app/generate_keys.sh

# Create a directory for certificates
RUN mkdir -p /app/certs

EXPOSE 8080
ENTRYPOINT ["/app/generate_keys.sh", "java", "-jar", "app.jar"]