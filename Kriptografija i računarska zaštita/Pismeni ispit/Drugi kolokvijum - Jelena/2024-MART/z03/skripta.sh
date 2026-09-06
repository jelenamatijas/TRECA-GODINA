#!/bin/bash
stores=keystore*

openssl base64 -d -in envelopa.txt -out envelopa.dec

for store in $stores; do
    keytool -importkeystore -srckeystore "$store" -destkeystore "p12$store" -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost
    openssl pkcs12 -in "p12$store" -out private.key -passin pass:sigurnost -nocerts -nodes
    openssl rsautl -decrypt -in envelopa.dec -out decrypt.bin -inkey private.key -passin pass:sigurnost 2>>error.log
    echo "$store : "
    cat decrypt.bin
done

#keystore19.jks : USPJESNO STE OTKLJUCALI ENVELOPU!

