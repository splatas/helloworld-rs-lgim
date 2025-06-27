# Base official image of JBoss EAP 7.4 with OpenJDK8
FROM registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6

# Temp user to copy files.
USER 0

# WAR file to be deployed in JBOSS
COPY ./target/helloworld-rs-lgim.war /opt/eap/standalone/deployments/

# DATABASE Drivers --------------------------------------------
COPY ./drivers/postgresql-42.7.7.jar /opt/eap/drivers/postgresql-42.7.7.jar
# DATABASE Drivers --------------------------------------------

# JBOSS EAP customization --------------------------------------------
COPY ./drivers/extensions.cli /opt/eap/extensions/extensions.cli
COPY ./drivers/postconfigure.sh /opt/eap/extensions/postconfigure.sh

RUN chown -R jboss:root /opt/eap/extensions && chmod -R g+rwx /opt/eap/extensions
# JBOSS EAP customization --------------------------------------------

# Permissions needed for no-root users in OpenShift can access
RUN chown -R jboss:root /opt/eap/standalone/deployments && chmod -R g+rw /opt/eap/standalone/deployments

USER 185
