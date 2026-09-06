#!/bin/bash

keys=kljuc*

clienthash=`openssl x509 -noout -modulus -in klijent.crt | openssl md5`

for key in $keys; do
    hash=`openssl rsa -noout -modulus -in "$key" | openssl md5`
    if [[ "$hash" == "$clienthash" ]]; then
        echo "$key"
    fi
done
#kljuc17.key

