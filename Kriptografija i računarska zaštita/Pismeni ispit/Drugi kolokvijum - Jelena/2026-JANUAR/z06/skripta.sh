#!/bin/bash

keys=kljuc*

m1=`openssl x509 -in rootca.pem -modulus -noout 2>error.log`

for key in $keys; do
    m2=`openssl rsa -in "$key" -modulus -noout 2>error.log`
    if [[ "$m1" == "$m2" ]]; then
        echo "$key"
    fi
done

#kljuc9.key
