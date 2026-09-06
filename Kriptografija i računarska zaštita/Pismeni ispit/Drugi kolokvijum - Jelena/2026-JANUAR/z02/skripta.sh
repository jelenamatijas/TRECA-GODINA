#!/bin/bash


dats=ulaz*
otisak=`cat kontrolni_hash.txt`

for dat in $dats; do
    hashdat=`openssl dgst -blake2s256 "$dat" | cut -d' ' -f2`
    if [[ "$hashdat" == "$otisak" ]]; then
        echo "$dat"
        passwd=`openssl dgst -sha1 $dat | cut -d' ' -f2`
        openssl enc -aes-192-ctr -d -in sifrat.txt -pass pass:$passwd
    fi
done

#ulaz12.txt
#Ulazni sadrzaj 12


