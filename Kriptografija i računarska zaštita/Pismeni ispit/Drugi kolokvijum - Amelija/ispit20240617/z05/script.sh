#!/bin/bash
stores=keystore*

for store in $stores; do
    output=`openssl dgst -sha3-512 -prverify kljuc.key -signature potpis.txt "$store" 2>>error.log`
    if [[ "$output" == "Verified OK" ]]; then
    echo "$store"
fi
done

#keystore18
