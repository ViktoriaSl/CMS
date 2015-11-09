#!/bin/bash

DOCKER_FOLDER_ADDR="./docker"
GITLAB_DOCKER_COMPOSE_FILE="$DOCKER_FOLDER_ADDR/docker-compose-gitlab.yml"
WILDFLY_DOCKER_COMPOSE_FILE="$DOCKER_FOLDER_ADDR/docker-compose-wildfly-mysql.yml"

docker-compose -f "$GITLAB_DOCKER_COMPOSE_FILE"     stop
docker-compose -f "$WILDFLY_DOCKER_COMPOSE_FILE"    stop


docker-compose -f "$GITLAB_DOCKER_COMPOSE_FILE"     rm
docker-compose -f "$WILDFLY_DOCKER_COMPOSE_FILE"    rm
