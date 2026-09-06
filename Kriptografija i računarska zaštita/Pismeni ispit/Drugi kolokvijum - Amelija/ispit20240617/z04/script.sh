#!/bin/bash

otisci=otisak*
ulazi=ulaz*

algs=`openssl dgst -list | grep '^-sha'`

for ulaz in $ulazi; do
    for otisak in $otisci; do
        hash1=`cat "$otisak"`
        for alg in $algs; do
            hash=`openssl dgst "$alg" "$ulaz" | cut -d' ' -f2`
            if [[ "$hash1" == "$hash" ]]; then
        echo "$alg : $ulaz : $otisak"
fi
done
done
done
   # sha3-224 ulaz1 otisak3
#shake256 ulaz23 otisak1
