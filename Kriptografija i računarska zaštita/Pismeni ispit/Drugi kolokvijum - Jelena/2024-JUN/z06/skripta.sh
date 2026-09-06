#!/bin/bash

otisak=`openssl rsa -in kljuc.key -modulus -noout | openssl md5`

stores=store*

for store in $stores; do
    m=`openssl pkcs12 -in "$store" -passin pass:sigurnost -clcerts -nokeys 2>/dev/null | openssl x509 -modulus -noout | openssl md5`
    if [[ "$m" == "$otisak" ]]; then
        echo "$store"
    fi
done

#store44.p12
