#!/bin/bash 
potpisi=potpis*
keys=kljuc*

for potpis in $potpisi; do
    for key in $keys; do
       openssl dsa -in "$key" -pubout -out public.key 
        output=`openssl dgst -sha224 -verify public.key -signature "$potpis" ulaz.txt 2>>error.log`
        if [[ "$output" == "Verified OK" ]]; then
        echo "$potpis : $key "
        fi
done 
done

potpis43 kljuc11
