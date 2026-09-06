#!/bin/bash

stores=*.jks

for store in $stores; do
    echo "$store"
    out=`keytool -list -v -keystore "$store" -storepass sigurnost 2>>error.log`
    if echo "$out" | grep -qi "server" && echo "$out" | grep -qi "client"; then
        echo "$out" | grep -A 5 -i "ExtendedKeyUsages"
    fi
done

#keystore18.jks
