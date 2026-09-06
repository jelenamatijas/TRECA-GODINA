#!/bin/bash

certHash=`openssl x509 -noout -modulus -in klijent.crt | openssl md5`
keys=kljuc*

for key in $keys; do
    keyHash=`openssl rsa -noout -modulus -in "$key" 2>>error.log | openssl md5`
    if [[ "$certHash" == "$keyHash" ]]; then
        echo "$key"
    fi
done

#kljuc48.key
