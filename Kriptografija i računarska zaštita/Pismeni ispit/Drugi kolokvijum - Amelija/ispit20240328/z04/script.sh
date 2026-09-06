#!/bin/bash

clients=client*
ca=`openssl x509 -in cacert.pem -noout -subject -nocert | sed 's/subject//'`


for client in $clients; do
    issuer=`openssl x509 -in "$client" -noout -issuer -nocert | sed 's/issuer//'`

    if [[ "$issuer" == "$ca" ]];
then echo "$client"
fi
done

#rjeseno
