FROM openjdk:17-alpine

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
COPY pom.xml /root

#RUN apt-get update && apt-get install dos2unix && dos2unix /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh
RUN apk update && apk add --upgrade maven bash dos2unix
RUN dos2unix /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh

#Start application
WORKDIR /root
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
