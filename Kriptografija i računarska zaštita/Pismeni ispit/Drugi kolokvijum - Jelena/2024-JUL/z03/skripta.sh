#!/bin/bash

ca=`openssl x509 -in cacert.pem -noout -subject -nocert | sed 's/subject//'`

clients=client*

for client in $clients; do
    issuer=`openssl x509 -in "$client" -noout -nocert -issuer | sed 's/issuer//'`
    if [[ "$issuer" == "$ca" ]]; then
        echo "$client"
    fi
done

#clientcert10.crt
#clientcert12.crt
#clientcert16.crt
#clientcert17.crt
#clientcert1.crt
#clientcert20.crt
#clientcert28.crt
#clientcert2.crt
#clientcert3.crt
#clientcert4.crt

