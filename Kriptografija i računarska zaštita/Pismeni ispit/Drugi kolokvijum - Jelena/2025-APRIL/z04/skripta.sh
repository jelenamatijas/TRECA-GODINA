#!/bin/bash

stores=keystore*.jks

for store in $stores; do
    keytool -importkeystore -srckeystore "$store" -destkeystore "p12_$store" -srcstoretype JKS -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost
    openssl pkcs12 -in "p12_$store" -out private.key -passin pass:sigurnost -nodes -nocerts
    openssl rsautl -decrypt -in envelopa.dec -inkey private.key -out decrypt.bin -passin pass:sigurnost
    echo "$store"
    cat decrypt.bin
done

#keystore39.jks
#OVO JE SKRIVENI TEKST VEZAN ZA JEDAN ISPITNI ZADATAK
