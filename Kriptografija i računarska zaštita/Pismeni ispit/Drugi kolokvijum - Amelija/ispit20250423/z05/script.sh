#!/bin/bash

tekstovi=tekst*
keys=kljuc*
algs=`openssl dgst -list | grep "^-sha"`
for tekst in $tekstovi; do
    for key in $keys;do
    for alg in $algs;do
    openssl rsa -in "$key" -pubout -out "$key.pub" 2>>error.log
    output=`openssl dgst "$alg" -verify "$key.pub" -signature potpis.txt "$tekst" 2>>error.log`
    if [[ "$output" == "Verified OK" ]]; then 
    echo "$tekst : $key"
    fi
done
done
done

#tekst20 kljuc19
