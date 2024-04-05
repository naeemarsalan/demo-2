FROM gradle:8.2.1-jdk17
WORKDIR /opt/crm
COPY ./ .
RUN ./gradlew installDist -x test
CMD ["java", "-jar", "build/libs/crm-1.1.0.jar"]