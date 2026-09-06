#!/bin/bash

hash=`openssl x509 -in "ca.crt" -noout -modulus | openssl md5 | awk '{print $NF}'`

keys=kljuc*

for key in $keys; do
    hashk=`openssl rsa -in "$key" -noout -modulus | openssl md5 | awk '{print $NF}'`
    if [[ "$hash" == "$hashk" ]]; then
        echo "$key"
    fi
done

#kljuc43.key

#--------------------------------------- ILI ---------------------------------------

algs=`openssl dgst -list | grep sha`
otisak=`cat otisak.dec`
#echo "$otisak"

for key in $keys; do
    for alg in $algs; do
        hash=`openssl dgst "$alg" "$key" 2>errors.log | cut -d' ' -f2`
        #echo "$hash"
        if [[ "$hash" == "$otisak" ]]; then
            echo "$key"
        fi
    done
done

#kljuc43.key

