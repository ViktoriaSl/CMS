#!/bin/bash

# Usage: execute.sh [Demo mode] [WildFly mode] [configuration file] [App properties file]
#
# For the app demo mode first arg need to be 'demo', otherwise it will be production.
# The default mode is 'standalone' and default configuration is based on the
# mode. It can be 'standalone.xml' or 'domain.xml'.
# The default properties file is 'application.properties'. The file is created
# in JBOSS_HOME/JBOSS_MODE/configuration folder.

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
APP_MODE=${1:-""}
JBOSS_MODE=${2:-"standalone"}
JBOSS_CONFIG=${3:-"$JBOSS_MODE.xml"}

APP_PROPS_FILE=${4:-"application.properties"}

function wait_for_server() {
  until $("$JBOSS_CLI" -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running); do
    sleep 1
  done
}

echo "=> Starting WildFly server"
$JBOSS_HOME/bin/$JBOSS_MODE.sh -b 0.0.0.0 -c $JBOSS_CONFIG &

echo "=> Waiting for the server to boot"
wait_for_server

# Add application properties to WildFly config directory
echo "$(cat /opt/jboss/wildfly/customization/config/gitlab.properties)" >> $JBOSS_HOME/$JBOSS_MODE/configuration/$APP_PROPS_FILE
echo "$(cat /opt/jboss/wildfly/customization/config/mail.properties)" >> $JBOSS_HOME/$JBOSS_MODE/configuration/$APP_PROPS_FILE
echo "$(cat /opt/jboss/wildfly/customization/config/google.properties)" >> $JBOSS_HOME/$JBOSS_MODE/configuration/$APP_PROPS_FILE

#CONNECTION_URL="jdbc:mysql://${MYSQL_SERVICE_SERVICE_HOST:=$DB_PORT_3306_TCP_ADDR}:${MYSQL_SERVICE_SERVICE_PORT:=$DB_PORT_3306_TCP_PORT}/sample"

$JBOSS_CLI -c << EOF
batch

set CONNECTION_URL=jdbc:mysql://$MYSQL_SERVICE_SERVICE_HOST:$MYSQL_SERVICE_SERVICE_PORT/sample
echo "Connection URL: " $CONNECTION_URL

# Add MySQL module
module add --name=com.mysql --resources=/opt/jboss/wildfly/customization/mysql-connector-java-5.1.31-bin.jar --dependencies=javax.api,javax.transaction.api

# Add MySQL driver
/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource)

# Add the datasource
data-source add --name=mysqlDS --driver-name=mysql --jndi-name=java:/MySqlDS --connection-url=jdbc:mysql://$DB_PORT_3306_TCP_ADDR:$DB_PORT_3306_TCP_PORT/cms --user-name=mysql --password=mysql --use-ccm=false --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true

# Execute the batch
run-batch
EOF

$JBOSS_CLI -c << EOF
  /system-property=application.properties:add(value=$JBOSS_HOME/$JBOSS_MODE/configuration/application.properties)
EOF

# Deploy the WAR
if ["$APP_MODE" = "demo" ]; then
  cp /opt/jboss/wildfly/customization/demo/cms-core-1.0.war $JBOSS_HOME/$JBOSS_MODE/deployments/cms-core-1.0.war
else
  cp /opt/jboss/wildfly/customization/cms-core-1.0.war $JBOSS_HOME/$JBOSS_MODE/deployments/cms-core-1.0.war
fi

echo "=> Shutting down WildFly"
if [ "$JBOSS_MODE" = "standalone" ]; then
  $JBOSS_CLI -c ":shutdown"
else
  $JBOSS_CLI -c "/host=*:shutdown"
fi

echo "=> Restarting WildFly"
$JBOSS_HOME/bin/$JBOSS_MODE.sh -b 0.0.0.0 -c $JBOSS_CONFIG