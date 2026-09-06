#!/bin/bash
ulazi=ulaz*
otisci=otisak*
algs=`openssl dgst -list | grep "^-sha"`
for ulaz in $ulazi; do
    for alg in $algs;do
    ulazhash=`openssl dgst "$alg" "$ulaz" | cut -d' ' -f2`
    for otisak in $otisci;do
    hash=`cat "$otisak"`
    if [[ "$ulazhash" == "$hash" ]]; then
    echo "$ulaz : $otisak"
fi
done
done
        done

ulaz37 otisak6
