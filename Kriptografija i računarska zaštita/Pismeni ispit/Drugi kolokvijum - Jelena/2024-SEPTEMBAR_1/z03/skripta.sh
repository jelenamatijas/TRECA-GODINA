#!/bin/bash

stores=keystore*

for store in $stores; do
    keytool -importkeystore -srckeystore "$store" -destkeystore "p12_$store" -deststoretype PKCS12 -srcstoretype JKS -srcstorepass sigurnost -deststorepass sigurnost
    openssl pkcs12 -in "p12_$store" -out private.key -passin pass:sigurnost -nocerts -nodes
    openssl rsautl -decrypt -in envelopa.dec -out decrypt.bin -inkey private.key -passin pass:sigurnost 2>>errors.log
    echo "$store:"
    cat decrypt.bin
done

#keystore37.jks:
#USPJESNO STE OTKLJUCALI ENVELOPU!

