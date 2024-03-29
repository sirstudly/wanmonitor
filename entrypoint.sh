#!/bin/sh
# You can set here anything you need before starting the spring boot application
#mvn spring-boot:run   -- disabled as it requires Chrome installed (which doesn't work with alpine linux)
rm -rf /root/target/*  # mvn clean unable to delete directory mount so just delete manually
mvn install

# build complete; keep the container running
#tail -f /dev/null