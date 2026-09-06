#!/bin/bash

stores=keystore*

for store in $stores; do
    keytool -importkeystore -srckeystore "$store" -destkeystore "p12_$store" -srcstoretype JKS -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost
    openssl pkcs12 -in "p12_$store" -nocerts -nodes -out private.key -passin pass:"sigurnost"
    echo "$store"
    openssl rsautl -decrypt -in envelopa.txt -inkey private.key
done    

#keystore41.jks
#BRZO NA SLJEDECI ZADATAK ;-)
