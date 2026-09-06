#!/bin/bash

otisak=`cat otisak.dec`
ulazi=ulaz*

for ulaz in $ulazi; do
    hash=`openssl dgst -sha-256 "$ulaz" | cut -d' ' -f2`    
    if [[ "$hash" == "$otisak" ]]; then
        echo "$ulaz"
    fi
done

#ulaz13.txt
