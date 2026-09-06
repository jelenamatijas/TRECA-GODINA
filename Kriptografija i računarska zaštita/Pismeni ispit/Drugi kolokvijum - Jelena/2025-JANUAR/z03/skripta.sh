#!/bin/bash

otisci=otisak*

algs=`openssl dgst -list | grep sha`

for otisak in $otisci; do
    output=`openssl base64 -d -in "$otisak"`
    for alg in $algs; do
        hash=`openssl dgst "$alg" ulaz.txt | cut -d' ' -f2`
        echo "$hash"
        if [[ "$hash" == "$output" ]]; then
            echo "$otisak" : "$alg"
        fi
    done
done
    
#otisak07.txt

