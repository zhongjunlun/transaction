FROM openjdk:21-jdk

WORKDIR /app

COPY target/bank-transaction-0.0.1-SNAPSHOT.jar /app/bank-transaction.jar

EXPOSE 8099

ENTRYPOINT ["java", "-jar", "/app/bank-transaction.jar"]