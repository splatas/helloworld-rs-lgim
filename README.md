# Instructions

## Login en OpenShift
oc login ...

## Create a new project
oc new-project lgim-eap

## Create image from Dockerfile (access to registry.redhat.io is required)
```shell
oc new-build --name=helloworld-eap-lgim --strategy=docker --binary --to=helloworld-eap-lgim:1.0
```

```shell
oc get bc
NAME                          TYPE     FROM         LATEST
helloworld-eap-lgim           Docker   Binary       2
```

## Launch start-build with local folder (--from-dir=.) as reference
```shell
oc start-build helloworld-eap-lgim --from-dir=. --follow

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

```shell
oc get is
NAME                    IMAGE REPOSITORY                                                                        TAGS     UPDATED
helloworld-eap-lgim     image-registry.openshift-image-registry.svc:5000/lgim-eap/helloworld-eap-lgim           1.0      13 minutes ago
```

## Create an app with the built image recently
```shell
oc new-app helloworld-eap-lgim:1.0

OUTPUT:
--> Found image deeb4bf (15 minutes old) in image stream "lgim-eap/helloworld-eap-lgim" under tag "1.0" for "helloworld-eap-lgim:1.0"

    JBoss EAP 7.4 
    ------------- 
    Platform for building and running JavaEE applications on JBoss EAP 7.4

    Tags: builder, javaee, eap, eap7


--> Creating resources ...
    deployment.apps "helloworld-eap-lgim" created
    service "helloworld-eap-lgim" created
--> Success
    Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
     'oc expose service/helloworld-eap-lgim' 
    Run 'oc status' to view your app.
```

## Expose the service with a Route
```shell
oc expose svc/helloworld-eap-lgim

OUTPUT:
route.route.openshift.io/helloworld-eap-lgim exposed
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

## Check the following curl command:
```shell
curl http://helloworld-eap-lgim-lgim-eap.apps.cluster-jh9xn.jh9xn.sandbox508.opentlc.com/helloworld-rs-lgim/public

OUTPUT:
public
```

```shell
curl http://helloworld-eap-lgim-lgim-eap.apps.cluster-jh9xn.jh9xn.sandbox508.opentlc.com/helloworld-rs-lgim/secure

OUTPUT:
<html><head><title>Error</title></head><body>Internal Server Error</body></html>
```

Second curl command fails due to security questions.

# Database integration

## First of all deploy a Postgresql (Ephemeral) DB fromm Openshift catalog.

## Get connection data from Secret 'postgreql':

database-name=sampledb
database-user=userYCR
database-password=22bvaR2dfuHW5weG

Connection string:
postgresql://<user>:<password>@<host>:<port>/<database>

postgresql://userYCR:22bvaR2dfuHW5weG@postgresql.lgim-eap.svc.cluster.local:5432/sampledb

JDBC: jdbc:postgresql://${DB_SERVICE_HOST}:${DB_SERVICE_PORT}/${DB_NAME}

## Testing conecction from a pod with 'psql' tool:
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

1. Create configmap
```shell
oc create configmap jboss-cli --from-file=postconfigure.sh=extensions/postconfigure.sh --from-file=extensions.cli=extensions/extensions.cli

OUTPUT:
configmap/jboss-cli created
```

2. Mount the configmap into the pods via the deployment 
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

--------------------------------
REVISAR!!!
    Modify app Deployment to add a reference to ConfigMap and Secret previously created:
    ```yaml
        env:
        - name: POSTGRESQL_USER
            valueFrom:
            secretKeyRef:
                name: db-secret
                key: DB_USER
        - name: POSTGRESQL_PASSWORD
            valueFrom:
            secretKeyRef:
                name: db-secret
                key: DB_PASSWORD
        - name: POSTGRESQL_DATABASE
            valueFrom:
            secretKeyRef:
                name: postgresql
                key: database-name

    ```
