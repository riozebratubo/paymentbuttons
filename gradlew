#!/bin/sh
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
APP_BASE_NAME=`basename "$0"`
exec "/usr/bin/env" "bash" "$0" "$@"
