#!/bin/bash

cacert=`openssl x509 -in cacert.pem -noout -subject -nocert | sed 's/subject//'`

clientcerts=client*

for clientcert in $clientcerts; do
    issuer=`openssl x509 -in "$clientcert" -noout -issuer -nocert | sed 's/issuer//'`
    if [[ "$issuer" == "$cacert" ]]; then
        echo "$clientcert"
    fi
done

#clientcert10.crt
#clientcert12.crt
#clientcert16.crt
#clientcert17.crt
#clientcert18.crt
#clientcert1.crt
#clientcert20.crt
#clientcert3.crt
#clientcert4.crt
#clientcert8.crt

