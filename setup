#!/usr/bin/env sh

echo ""
echo "-------------------------------------------------------------------------"
echo "Building application"
echo "-------------------------------------------------------------------------"
echo ""

docker-compose up --build -d

echo ""
echo "-------------------------------------------------------------------------"
echo "Creating database and example data"
echo "-------------------------------------------------------------------------"
echo ""

docker-compose exec whattodo-app java -jar dropwizard-what-to-do.jar db migrate config-prod.yml

echo ""
echo "-------------------------------------------------------------------------"
echo "Shutting down"
echo "-------------------------------------------------------------------------"
echo ""

docker-compose down