#!/usr/bin/env sh

echo "---------------------"
echo "Building application"
echo "---------------------"

docker-compose up --build -d

echo "----------------------------------"
echo "Creating database and example data"
echo "----------------------------------"

docker-compose exec whattodo-app java -jar dropwizard-what-to-do.jar db migrate config-prod.yml

echo "-------------"
echo "Shutting down"
echo "-------------"

docker-compose down