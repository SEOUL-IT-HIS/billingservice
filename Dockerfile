FROM eclipse-temurin:17-jdk

RUN mkdir /app
WORKDIR /app

ADD ./build/libs/*.jar /app/app.jar

EXPOSE 80
ENTRYPOINT ["java","-jar","app.jar"]