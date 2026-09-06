#!/bin/bash

otisci=otisak*
ulazi=ulaz*

algs=`openssl list -digest-commands`

for ulaz in $ulazi; do
     for otisak in $otisci; do
        hash1=`cat $otisak`
        for alg in $algs; do
            hash=`openssl dgst -$alg "$ulaz" | cut -d' ' -f2`
            if [[ "$hash" == "$hash1" ]]; then
                echo "$alg :$ulaz : $otisak"
            fi
        done
     done
done
#-sha3-224 :ulaz1.txt : otisak3.txt
#blake2b512 :ulaz41.txt : otisak4.txt
