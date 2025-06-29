# Instructions

## Login en OpenShift
```shell
oc login ...
```

## Create a new project: 
Don't do it if you already have an assigned proyect (in that case run 'oc project PROJECT_NAME')

```shell
oc new-project lgim-eap
```

## Create an ImagesStrem and a BuilConfig for your custom image.
```shell
oc apply -f ./ocp/30.helloworld-eap-lgim-docker.is.yaml -n lgim-eap 
```

And then:
```shell
oc apply -f ./ocp/31.helloworld-eap-lgim-docker.bc -n lgim-eap 
```

```shell
oc get bc
NAME                          TYPE     FROM         LATEST
helloworld-eap-lgim-docker    Docker   Binary       2
```


**Just for reference**: another option is runnig the following command (access to registry.redhat.io is required)
**DON'T RUN THIS COMMAND**
```shell
oc new-build --name=helloworld-eap-lgim --strategy=docker --binary --to=helloworld-eap-lgim:1.0
```
**DON'T RUN THIS COMMAND**

## Run the Maven command to create your app, running: 
```shell
mvn clean install -Dcheckstyle.skip=true
```

Now you should have a 'target' folder with you app file inside: 'helloworld-rs-lgim-sources.war'


## Dockerfile
Lets see the conent of our ./Dockerfile:

```dockefile
# Base official image of JBoss EAP 7.4 with OpenJDK8
FROM registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6

# Temp user to copy files.
USER 0

# WAR file to be deployed in JBOSS
COPY ./target/helloworld-rs-lgim.war /opt/eap/standalone/deployments/

# DATABASE Drivers --------------------------------------------
COPY ./extensions/postgresql-42.7.7.jar /opt/eap/extensions/postgresql-42.7.7.jar
# DATABASE Drivers --------------------------------------------

# JBOSS EAP customization --------------------------------------------
COPY ./extensions/extensions.cli /opt/eap/extensions/extensions.cli
COPY ./extensions/postconfigure.sh /opt/eap/extensions/postconfigure.sh

RUN chown -R jboss:root /opt/eap/extensions && chmod -R g+rwx /opt/eap/extensions
# JBOSS EAP customization --------------------------------------------

# Permissions needed for no-root users in OpenShift can access
RUN chown -R jboss:root /opt/eap/standalone/deployments && chmod -R g+rw /opt/eap/standalone/deployments

USER 185
```

You can see that:
- Base image is: registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6
- Our app file (.jar, .war or .ear) is in the right deployment folder: /opt/eap/standalone/deployments/
- Driver for our database (not included in Base image) is in an specific folder '/opt/eap/extensions/': in this case 'postgresql-42.7.7.jar'
- Two configuration files are included in the same folder: 'extensions.cli' (with jboss-cli instructions) and 'postconfigure.sh' is an script to apply the 'extensions.cli' in our instance of JBOSS EAP.
    **Pay special attentiont on jboss-cli** where you can find all authentication details for our DB connection. You will need to apply the right information there, previuos to run 'start-build' command.
- Some instructions to grant permissions


## Launch start-build with local folder (--from-dir=.) as reference
With this command you will launch the construction of our custom image in refernece of our BuildConfig(./ocp/31.helloworld-eap-lgim-docker.bc).
The strategy of building is defined as 'Docker', so we provide our local ./Dockerfile to guide the construction.
As a result we will have an ImageStream defined by ./ocp/30.helloworld-eap-lgim-docker.is.yaml: 'helloworld-eap-lgim-docker' 

```shell
oc start-build helloworld-eap-lgim-docker --from-dir=. --follow

OUTPUT:
Pulling image registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6 ...
Trying to pull registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6...
Getting image source signatures
Copying blob sha256:555dcd4950a9bd98d92abdeeb7c514404ea9840a353a95a080f43d801fa64d79
Copying blob sha256:43ddc3bd12b8691687c9f6c273331ca07e3a89b50a619e0db9b040a8a629386d
Copying config sha256:ec978aeaf028c5455cc19841bcd0d7205815dc1eb3f3931ff77e267b4c009fc1
Writing manifest to image destination
Adding transient rw bind mount for /run/secrets/rhsm
STEP 1/7: FROM registry.redhat.io/jboss-eap-7/eap74-openjdk8-openshift-rhel8:7.4.22-6
STEP 2/7: USER 0
--> 6ed23f8e43db
STEP 3/7: COPY ./target/helloworld-rs-lgim.war /opt/eap/standalone/deployments/
--> d8baad68c6a5
STEP 4/7: RUN chown -R jboss:root /opt/eap/standalone/deployments && chmod -R g+rw /opt/eap/standalone/deployments
--> 9279833fd86e
STEP 5/7: USER 185
--> 482849f5fc69
STEP 6/7: ENV "OPENSHIFT_BUILD_NAME"="helloworld-eap-lgim-2" "OPENSHIFT_BUILD_NAMESPACE"="lgim-eap"
--> 0b9be15170d7
STEP 7/7: LABEL "io.openshift.build.name"="helloworld-eap-lgim-2" "io.openshift.build.namespace"="lgim-eap"
COMMIT temp.builder.openshift.io/lgim-eap/helloworld-eap-lgim-2:973ab850
--> deeb4bfd3c8e
Successfully tagged temp.builder.openshift.io/lgim-eap/helloworld-eap-lgim-2:973ab850
deeb4bfd3c8e9ed98c8e3606d9df55eefd125c641d60458f29f58c29b8c256c6
...
Pushing image image-registry.openshift-image-registry.svc:5000/lgim-eap/helloworld-eap-lgim:1.0 ...
Getting image source signatures
Copying blob sha256:415b7206848ac1a6251d9884be87c9934f0a9bcb4028e81a9e83318cd603933b
Copying blob sha256:43ddc3bd12b8691687c9f6c273331ca07e3a89b50a619e0db9b040a8a629386d
Copying blob sha256:555dcd4950a9bd98d92abdeeb7c514404ea9840a353a95a080f43d801fa64d79
Copying blob sha256:6cdf9096870de46920daef207163e3a59ae4f9d55c11467796f9757e70a8a94a
Copying config sha256:deeb4bfd3c8e9ed98c8e3606d9df55eefd125c641d60458f29f58c29b8c256c6
Writing manifest to image destination
Successfully pushed image-registry.openshift-image-registry.svc:5000/lgim-eap/helloworld-eap-lgim@sha256:203e50a78d6bae5ae79d3a8c6b0d52c30cd1353300e583a6eaad15deccfa0e27
Push successful
```

At this point you have an image based on 'eap74-openjdk8-openshift-rhel8:7.4.22-6' with your app and JBOSS EAP configuration included in your custom image.

```shell
oc get is
NAME                        IMAGE REPOSITORY                                                                        TAGS        UPDATED
helloworld-eap-lgim-docker  image-registry.openshift-image-registry.svc:5000/lgim-eap/helloworld-eap-lgim-docker    latest,1.0  13 minutes ago
```

If you don't see a running pod, you should check the logs and the 'extensions.cli' file.

## Create a Deployment with the image built recently
```shell
oc apply -f ./ocp/02.helloworld-eap-lgim.deployment.yaml
```

## Verification
```shell
oc get all -l app=helloworld-eap-lgim

OUTPUT:
NAME                          TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)                      AGE
service/helloworld-eap-lgim   ClusterIP   172.30.87.64   <none>        8080/TCP,8443/TCP,8778/TCP   11m

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/helloworld-eap-lgim   1/1     1            1           11m

NAME                                           HOST/PORT                                                                      PATH   SERVICES              PORT       TERMINATION   WILDCARD
route.route.openshift.io/helloworld-eap-lgim   helloworld-eap-lgim-lgim-eap.apps.cluster-jh9xn.jh9xn.sandbox508.opentlc.com          helloworld-eap-lgim   8080-tcp                 None
```

# Database integration
For our testing we integrated our app with a local PostresSql database.

## First of all deploy a Postgresql (Ephemeral) DB fromm Openshift catalog.

## Get connection data from Secret 'postgreql':

database-name=sampledb
database-user=userYCR
database-password=22bvaR2dfuHW5weG

Connection string:
postgresql://<user>:<password>@<host>:<port>/<database>

postgresql://userYCR:22bvaR2dfuHW5weG@postgresql.lgim-eap.svc.cluster.local:5432/sampledb

JDBC: jdbc:postgresql://${DB_SERVICE_HOST}:${DB_SERVICE_PORT}/${DB_NAME}

## Testing conection from a pod with 'psql' tool:
```shell
psql -h postgresql -U userYCR -d sampledb

OUTPUT:
sh-4.4$ psql -h postgresql -U userYCR -d sampledb
Password for user userYCR: 
psql (10.23)
Type "help" for help.

sampledb=> 
```

## Create an example table
```shell
sampledb=> CREATE TABLE hello (
  id SERIAL PRIMARY KEY,
  service TEXT,
  response TEXT
);


OUTPUT:
sampledb=> CREATE TABLE hello (
sampledb(>   id SERIAL PRIMARY KEY,
sampledb(>   service TEXT,
sampledb(>   response TEXT
sampledb(> );
CREATE TABLE
```

## Insert some data in the new table
```shell
INSERT INTO hello (service, response)
VALUES ('public', 'Hello from PUBLIC service!'), ('secure', 'Hello from SECURE service!');

SELECT * FROM hello;

OUTPUT:
sampledb=> SELECT * FROM hello;
 id | service |          response          
----+---------+----------------------------
  1 | public  | Hello from PUBLIC service!
  2 | secure  | Hello from SECURE service!
(2 rows)
```

IMPORTANT: Remember you have an Postgresql EPHEMERAL instance. So, if the POD is restarted, YOU WILL LOSE THAT DATA.


## OCP objects to integrate the database
Create objects with DB credentials:

```shell
oc apply -f 01.database-credentials.yaml 

secret/db-secret created
```

Create objects with DB connection string:

References: https://docs.redhat.com/en/documentation/red_hat_jboss_enterprise_application_platform/7.4/html-single/getting_started_with_jboss_eap_for_openshift_container_platform/index#custom_scripts

1. Create configmap: **NOT MANDATORY (this configuration was included in Dockerfile as requested)**
```shell
oc create configmap jboss-cli --from-file=postconfigure.sh=extensions/postconfigure.sh --from-file=extensions.cli=extensions/extensions.cli

OUTPUT:
configmap/jboss-cli created
```

2. Mount the configmap into the pods via the deployment **NOT MANDATORY (this configuration was included in Dockerfile as requested)**
```shell
oc set volume deployment/helloworld-eap-lgim --add --name=jboss-cli -m /opt/eap/extensions -t configmap --configmap-name=jboss-cli --default-mode='0755' --overwrite
```

3. Applying the changes
Just delete current pod and changes should be applied:

```shell
oc get pods  (returns POD_NAME)
oc delete pod POD_NAME
```

4. Verify logs
You should see the following message "Running postconfigure" in new pod's log:
```shell
oc logs POD_NAME | grep "Running postconfigure"

@---->>>> Running postconfigure.sh
@---->>>> Running postconfigure.sh: END!!
```

This message means our configuration was applied.


4. Verify DB connection
You should see a new datasource defined in the JBOSS instance.
Run the foollowing commands:

```shell
oc rsh POD_NAME

sh-4.4$ $JBOSS_HOME/bin/jboss-cli.sh -c
[standalone@localhost:9990 /] /subsystem=datasources/data-source=PostgresDS:test-connection-in-pool
{
    "outcome" => "success",
    "result" => [true]
}
```

If you receive this message ("outcome" => "success")  it implies your connection with PostgresDS's datasource is working ok.

Extra verifications: datasource and driver created with ConfigMap 'jboss-cli'
```shell
[standalone@localhost:9990 /] ls -l /subsystem=datasources/data-source=
PostgresDS     (<= your datasource)


[standalone@localhost:9990 /] ls -l /subsystem=datasources/jdbc-driver=
h2
postgresql   (<= your DB driver)
```

# Rebuilding your app:
If you need to apply some changes in your application code follow these steps:

```shell
mvn clean install -Dcheckstyle.skip=true
```

```shell
oc start-build helloworld-eap-lgim-db --from-dir=. --follow
```


# Add users in JBOSS EAP (this should be include in 'jboss-cli' ConfigMap):
According with last discussion, we included a dummy user just for testing our secured endpoints.
**This instruction now is included in './ocp/postconfigure.sh' script**

```shell
/opt/eap/bin/add-user.sh -a -u john -p password123 -g USER -s
```

Once the pod is runnig you can check this running cat's commands to verify user and roles added:
```shell
cat /opt/eap/standalone/configuration/application-users.properties

OUTPUT:
# The following illustrates how an admin user could be defined, this
# is for illustration only and does not correspond to a usable password.
#
#admin=2a0923285184943425d1f53ddd58ec7a
john=d7f6fcf77760e34e2e9225f64545d5e8
```

```shell
cat /opt/eap/standalone/configuration/application-roles.properties

OUTPUT:
...
# The following illustrates how an admin user could be defined.
#
#admin=PowerUser,BillingAdmin,
#guest=guest
john=USER
```

## Add Security configuration to 'jboss-cli' ConfigMap:
Again this configuration is included in './ocp/extensions.cli' file as requested

```shell
# Security #
/subsystem=elytron/security-domain=ApplicationDomain:add(default-realm=ApplicationRealm, realms=[{realm=ApplicationRealm}], permission-mapper=default-permission-mapper)
/subsystem=undertow/application-security-domain=other:add(security-domain=ApplicationDomain)

quit
```

## This content is included in 'jboss-web.xml' (/src/main/webapp/WEB-INF/jboss-web.xml):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web>
    <security-domain>other</security-domain>
</jboss-web>
```

## Dummy user: this was applied previously (NOT NEEDED)
In order to test the authentication, we need to create a dummy user with 'add-user.sh' script in our JBOSS EAP instances.
To persist this, we add the sentence to our 'postconfigure.sh' script (in 'jboss-cli' ConfigMap)

```shell
#!/usr/bin/env bash
echo "@---->>>> Running postconfigure.sh"

$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/extensions/extensions.cli

# Add dummy user:
/opt/eap/bin/add-user.sh -a -u john -p password123 -g USER -s

echo "@---->>>> Running postconfigure.sh: END!!"
```

Finally we redeploy the application.

When try the following endpoint "http://helloworld-eap-lgim-lgim-eap.apps.cluster-jh9xn.jh9xn.sandbox508.opentlc.com/helloworld-rs-lgim/secure"
a pop-up should appears requesting user/password.

If we indicate the credentials added in 'postconfigure.sh' then we should get a response like this:
```shell
USER: john  ==> Hello from SECURE service!
```

But if we try with "http://helloworld-eap-lgim-lgim-eap.apps.cluster-jh9xn.jh9xn.sandbox508.opentlc.com/helloworld-rs-lgim/public"
the response should be: 
```shell
Hello from PUBLIC service!
```

(User is not required)