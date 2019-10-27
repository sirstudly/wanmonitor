FROM openjdk:8-jre-alpine

RUN apk update && apk upgrade
RUN apk add bash curl

COPY src/main/resources/config.properties src/main/resources/logback.xml src/main/resources/wanmonitor.sh /root/

# keep the container running
CMD tail -f /dev/null
