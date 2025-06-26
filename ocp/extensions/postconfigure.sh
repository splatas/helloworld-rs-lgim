#!/usr/bin/env bash
echo "@---->>>> Running postconfigure.sh"
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/extensions/extensions.cli
echo "@---->>>> Running postconfigure.sh: END!!"
