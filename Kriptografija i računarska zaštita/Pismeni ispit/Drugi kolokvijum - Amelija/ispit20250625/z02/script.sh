#!/bin/bash

ulazi=ulaz*
otisak=`cat otisak.txt`
algs=`openssl enc -list | tr ' ' '\n' | grep '^-camellia'`
echo "$algs"
for ulaz in $ulazi;do
    hash=`openssl dgst -shake256 "$ulaz" | cut -d' ' -f2`
    if [[ "$hash" == "$otisak" ]]; then
    echo "pronadjen : $ulaz"
    for alg in $algs; do
    openssl enc -d "$alg" -in sifrat.dec -pass pass:"$ulaz" -out decrypted.bin 2>> error.log
    echo "$alg : "
    cat decrypted.bin
    done
fi
done

ulaz55
192-ofb


