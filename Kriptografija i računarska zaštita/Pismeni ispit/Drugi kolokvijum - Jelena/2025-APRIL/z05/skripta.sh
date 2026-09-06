#!/bin/bash

keys=kljuc*
ulazi=tekst*
algs=`openssl list -digest-commands | grep sha`
potpis=`cat potpis.txt`

for key in $keys; do
    openssl rsa -in "$key" -pubout -out "$key.pub" 2>error.log
    for ulaz in $ulazi; do
        for alg in $algs; do
            rez=`openssl dgst -"$alg" -verify "$key.pub" -signature potpis.txt "$ulaz" 2>error.log`
            if [[ "$rez" == "Verified OK" ]]; then
                echo "$key":"$ulaz":"$alg"
            fi
        done
    done
done

#kljuc19.key:tekst20.txt:sha224
