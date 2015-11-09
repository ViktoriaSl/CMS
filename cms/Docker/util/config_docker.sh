#!/bin/bash
# Usage: config_docker.sh

BASE_FOLDER_ADDR="$(cd "$(dirname "$0")"/..; pwd)"
CONFIG_FOLDER_ADDR="$BASE_FOLDER_ADDR/config"
TEMPLATES_FOLDER_ADDR="$BASE_FOLDER_ADDR/templates"
DOCKER_FOLDER_ADDR="$BASE_FOLDER_ADDR/docker"
UTIL_FOLDER_ADDR="$BASE_FOLDER_ADDR/util/additional"

GITLAB_PROP_FILE="$CONFIG_FOLDER_ADDR/gitlab.properties"
WILDFLY_PROP_FILE="$CONFIG_FOLDER_ADDR/wildfly.properties"
MAIL_PROP_FILE="$CONFIG_FOLDER_ADDR/mail.properties"

GITLAB_TEMPLATE='docker-compose-gitlab.yml'
WILDFLY_TEMPLATE='docker-compose-wildfly-mysql.yml'

function get_property ()
{
    PROP_FILE=$1
    PROP_KEY=$2
    PROP_VALUE=$(cat "$PROP_FILE" | grep "$PROP_KEY" | tr -d '\r' | cut -d'=' -f2)
    echo "$PROP_VALUE"
}

# Copy docker-compose-gitlab file and set IP and PORT
cp "$TEMPLATES_FOLDER_ADDR/$GITLAB_TEMPLATE" "$DOCKER_FOLDER_ADDR/$GITLAB_TEMPLATE"
export GITLAB_IP=$(get_property "$GITLAB_PROP_FILE" gitlab_ip)
export GITLAB_PORT=$(get_property "$GITLAB_PROP_FILE" gitlab_port)
export SMTP_ADDRESS=$(get_property "$MAIL_PROP_FILE" mail_email)
export SMTP_PASSWORD=$(get_property "$MAIL_PROP_FILE" mail_password)
export SMTP_HOST=$(get_property "$MAIL_PROP_FILE" mail_host)
export SMTP_PORT=$(get_property "$MAIL_PROP_FILE" mail_port)

export GITLAB_ROOT_PASSWORD=$(get_property "$GITLAB_PROP_FILE" gitlab_root_password)
export GITLAB_ROOT_EMAIL=$(get_property "$GITLAB_PROP_FILE" gitlab_root_email)
export GITLAB_URL=$(get_property "$GITLAB_PROP_FILE" gitlab_host)

"$UTIL_FOLDER_ADDR/subst.sh"  "$DOCKER_FOLDER_ADDR/$GITLAB_TEMPLATE"


# Copy docker-compose-gitlab file and set IP and PORT
cp "$TEMPLATES_FOLDER_ADDR/$WILDFLY_TEMPLATE" "$DOCKER_FOLDER_ADDR/$WILDFLY_TEMPLATE"
export WILDFLY_IP=$(get_property "$WILDFLY_PROP_FILE" wildfly_ip)
export WILDFLY_PORT=$(get_property "$WILDFLY_PROP_FILE" wildfly_port)
export SMTP_PORT=$(get_property "$MAIL_PROP_FILE" mail_port)
"$UTIL_FOLDER_ADDR/subst.sh"  "$DOCKER_FOLDER_ADDR/$WILDFLY_TEMPLATE"

# Copy configuration directory into the docker folder
cp -r "$CONFIG_FOLDER_ADDR" "$DOCKER_FOLDER_ADDR"