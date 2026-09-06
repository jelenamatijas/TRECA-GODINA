#!/bin/bash

stores=keystore*

for store in $stores; do
keytool -importkeystore -srckeystore "$store" -destkeystore "$store".p12 -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost -noprompt 2>>error.log
openssl pkcs12 -in "$store".p12 -nocerts -nodes -out "$store.key" -legacy -passout pass:sigurnost -passin pass:sigurnost 2>>error.log
decrypt=`openssl rsautl -decrypt -in envelopa.dec -inkey "$store.key" 2>>error.log`
    
echo " $store : $decrypt"
done

keystore39
