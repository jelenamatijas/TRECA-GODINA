#!/bin/bash

envelopa="envelopa.txt"

openssl rsautl -decrypt -in "$envelopa" -inkey env_key.key -out key.key 2>>error.log

cat key.key

otisak=`openssl rsa -pubin -in key.key -modulus -noout 2>>error.log | openssl md5`

stores=store*

for store in $stores; do
    openssl pkcs12 -in "$store" -out cert.pem -passin pass:sigurnost -clcerts -nokeys -legacy
    m=`openssl x509 -in cert.pem -modulus -noout 2>>error.log | openssl md5`
    if [[ "$m" == "$otisak" ]]; then
        echo "$store"
    fi
done

#store50.p12
