#!/bin/bash

stores=keystore*

for store in $stores;do
    dest="dest_${store}.p12"
    key="key_${store}.pem"
    out="desifrovano_${store}.pem"
    keytool -importkeystore -srckeystore "$store" -srcstoretype JKS -destkeystore "$dest" -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost  2>>error.log
    openssl pkcs12 -in "$dest" -passin pass:sigurnost -nodes -nocerts -out "$key" -provider legacy -provider default 
openssl rsautl -decrypt -inkey "$key" -in envelopa.dec -out "$out"  2>>error.log
    echo " $store "
     cat "$out"

done

rjesenje:
keystore44
