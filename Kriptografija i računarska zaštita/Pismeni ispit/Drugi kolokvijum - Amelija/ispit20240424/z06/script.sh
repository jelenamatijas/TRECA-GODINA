#!/bin/bash
stores=keystore*
otisak=`cat otisak.dec`

for store in $stores; do
    hash=`openssl dgst -sha3-384 "$store" | cut -d' ' -f2`
    if [[ "$hash" == "$otisak" ]]; then
    echo "$store"
fi
done

#keystore5.jks
