#!/bin/bash

keys=kljuc*
cahash=`openssl x509 -in "klijent.crt" -noout -modulus 2>error.log | openssl md5 | awk '{print $NF}'`

for key in $keys; do
    hash=`openssl rsa -in "$key" -modulus -noout | openssl md5 | awk '{print $NF}'`
    if [[ "$hash" == "$cahash" ]]; then
        echo "$key"
    fi
done

#kljuc42.key
