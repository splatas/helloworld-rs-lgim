# Base official image of JBoss EAP 7.4 with OpenJDK8
FROM registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6

# Temp user to copy files.
USER 0

# WAR file to be deployed in JBOSS
COPY ./target/helloworld-rs-lgim.war /opt/eap/standalone/deployments/

# DATABASE Drivers --------------------------------------------
# JBOSS_HOME/
# └── modules/
#     └── org/
#         └── postgresql/
#             └── main/
COPY ./drivers/postgresql-42.7.7.jar /opt/eap/modules/org/postgresql/main/postgresql-42.7.7.jar
COPY ./drivers/module.xml /opt/eap/modules/org/postgresql/main/module.xml
# DATABASE Drivers --------------------------------------------

# Permissions needed for no-root users in OpenShift can access
RUN chown -R jboss:root /opt/eap/standalone/deployments && chmod -R g+rw /opt/eap/standalone/deployments

# DATABASE Drivers --------------------------------------------
COPY ./ocp/openshift-launch.sh /opt/eap/bin/
RUN chmod +x /opt/eap/bin/openshift-launch.sh
# DATABASE Drivers --------------------------------------------    

USER 185
