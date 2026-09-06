#!/bin/bash

ca=`openssl x509 -in cacert.pem -noout -subject | sed 's/subject//'`

certs=client*

for cert in $certs; do
    issuer=`openssl x509 -in "$cert" -noout -issuer | sed 's/issuer//'`
    if [[ "$issuer" == "$ca" ]]; then
        echo "$cert"
    fi
done

#clientcert10.crt
#clientcert12.crt
#clientcert16.crt
#clientcert17.crt
#clientcert1.crt
#clientcert23.crt
#clientcert30.crt
#clientcert3.crt
#clientcert4.crt
#clientcert8.crt

