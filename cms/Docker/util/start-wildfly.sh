#!/bin/bash

BASE_FOLDER_ADDR="$(cd "$(dirname "$0")"/..; pwd)"
DOCKER_FOLDER_ADDR="$BASE_FOLDER_ADDR/docker"
WILDFLY_DOCKER_COMPOSE_FILE="$DOCKER_FOLDER_ADDR/docker-compose-wildfly-mysql.yml"

cd "$DOCKER_FOLDER_ADDR"
docker-compose -f "$WILDFLY_DOCKER_COMPOSE_FILE" up -d