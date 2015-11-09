#!/bin/bash

UTIL_FOLDER_ADDR="./util"
cd "$UTIL_FOLDER_ADDR"

./config_docker.sh
./start-gitlab.sh
./start-wildfly.sh
