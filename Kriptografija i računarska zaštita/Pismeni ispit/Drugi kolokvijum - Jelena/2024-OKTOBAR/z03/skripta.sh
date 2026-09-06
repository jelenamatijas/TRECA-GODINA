#!/bin/bash

ulazi=ulaz*
openssl pkey -in kljuc.key -pubout -out pubkey.key -passin pass:"sigurnost"

for ulaz in $ulazi; do
    if openssl dgst -sha1 -verify pubkey.key -signature potpis.dec "$ulaz"; then
        echo "$ulaz"
    fi
done
    
#ulaz73.txt

algs=`openssl enc -list | grep des`
lozinka="ulaz73.txt"

temp=`mktemp`

for alg in $algs; do
    openssl enc -d $alg -in sifrat.dec -pass pass:"$lozinka" -out "$temp" 2>errors.log
    if cmp -s "ulaz73.txt" "$temp"; then
        echo "$alg"
    fi
done
    
