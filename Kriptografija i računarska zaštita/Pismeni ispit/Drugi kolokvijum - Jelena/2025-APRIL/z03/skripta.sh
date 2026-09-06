#!/bin/bash

otisci=otisak*
ulazi=ulaz*
algs=`openssl list -digest-commands`


for ulaz in $ulazi; do
    for otisak in $otisci; do
        hash=`cat "$otisak"`
        for alg in $algs; do
            rez=`openssl dgst -"$alg" "$ulaz" | cut -d' ' -f2`
            if [[ "$rez" == "$hash" ]]; then
                echo "$ulaz" : "$otisak"
            fi
        done
    done
done

#ulaz37.txt : otisak6.txt
