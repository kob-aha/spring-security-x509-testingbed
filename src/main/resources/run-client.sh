#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

curl -E ${DIR}/keystore/cid.crt.pem:changeit \
    --key ${DIR}/keystore/cid.key.pem \
    --cacert ${DIR}/keystore/ca.crt -vv \
    https://localhost:8443/greeting