#!/bin/bash

stores=keystore*
for store in $stores; do
    keytool -importkeystore -srckeystore "$store" -destkeystore "p12$store" -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost
    openssl pkcs12 -in "p12$store" -nocerts -legacy -passin pass:sigurnost -passout pass:sigurnost -out priv.key 
    output=`openssl rsautl -decrypt -inkey priv.key -passin pass:sigurnost -in envelopa.dec`
    echo " $store : $output"
done

#keystore20
