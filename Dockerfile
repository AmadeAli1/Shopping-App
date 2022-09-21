FROM maven:3.8-jdk-17 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN java -jar target/Shopping-App-0.0.1-SNAPSHOT.jar