#!/usr/bin/env sh

docker-compose up --build -d

docker-compose exec whattodo-app java -jar dropwizard-what-to-do.jar db migrate config-prod.yml

docker-compose down