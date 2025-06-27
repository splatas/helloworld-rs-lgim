#!/usr/bin/env bash
echo "@---->>>> Running postconfigure.sh"

$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/extensions/extensions.cli

# Dummy user for testing:
/opt/eap/bin/add-user.sh -a -u john -p password123 -g USER -s

echo "@---->>>> Running postconfigure.sh: END!!"