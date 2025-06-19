#!/bin/bash

# Aplicar configuración del datasource
/opt/eap/bin/jboss-cli.sh --connect --file=/opt/eap/extensions/configure-ds.cli

# Ejecutar EAP normalmente
exec /opt/eap/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
