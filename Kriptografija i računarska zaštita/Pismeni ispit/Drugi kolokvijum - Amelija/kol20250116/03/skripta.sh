#!/bin/bash

ulazdat="ulaz.txt"
potpisi=potpis*
keys=kljuc*

for potpis in $potpisi; do
    for key in $keys; do

        # extract public key
        openssl dsa -in "$key" -pubout -out pub.key 2>/dev/null 

        # verify signature
        message=`openssl dgst -sha224 -verify pub.key -signature "$potpis" "$ulazdat" 2>/dev/null`
          
        if [[ "$message" == "Verified OK" ]]; then
            echo " Potpis $potpis odgovara fajlu $ulazdat koristeći ključ $key"
        fi

    done
done

