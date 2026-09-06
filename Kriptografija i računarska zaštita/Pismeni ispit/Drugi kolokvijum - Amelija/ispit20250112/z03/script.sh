#!/bin/bash
otisci=otisak*
algs=`openssl dgst -list | grep "^-sha"`

for otisak in $otisci;do
    output=`openssl base64 -d -in "$otisak"`
    echo "$output"
    for alg in $algs; do
    hash=`openssl dgst "$alg" ulaz.txt | cut -d' ' -f2`
    if [[ "$hash" == "$output" ]]; then
    echo "$otisak"
fi
done

done

otisak07.txt
