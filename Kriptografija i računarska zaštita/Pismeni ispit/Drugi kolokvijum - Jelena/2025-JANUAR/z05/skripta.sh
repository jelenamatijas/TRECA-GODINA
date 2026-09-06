#!/bin/bash

stores=store*

for store in $stores; do
    if openssl pkcs12 -in "$store" -nokeys -passin pass:"sigurnost" | openssl x509 -noout -text | grep -A 2 "Extended Key Usage" ; then
        echo "$store"
    fi
done

#store27.p12
