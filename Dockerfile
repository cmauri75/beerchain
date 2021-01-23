FROM openjdk:11
ARG JAR_FILE=target/*.jar

COPY target/birchain*.jar /opt/app/birchain.jar

#ENTRYPOINT ["java","-jar","/birchain.jar"]
CMD cd /opt/app && java -jar birchain.jar $NODE_URL 8888