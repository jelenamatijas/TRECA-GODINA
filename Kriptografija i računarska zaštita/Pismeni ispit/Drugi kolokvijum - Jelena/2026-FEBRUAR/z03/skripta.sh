#!/bin/bash

jksinfo=`keytool -list -v -keystore keystore.jks -storepass "sigurnost" 2>error.log`


for cert in client*; do
    fp=`openssl x509 -in "$cert" -noout -fingerprint -sha256 | cut -d'=' -f2`
    if echo $jksinfo | grep -qi "$fp"; then
        echo "$cert"
    fi
done

#client29.crt
#client9.crt
