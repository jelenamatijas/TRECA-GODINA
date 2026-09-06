#!/bin/bash

otisci=otisak*
ulazi=ulaz*

for otisak in $otisci; do
    otisakhash=`cat $otisak`
    count=`cat "$otisak" | tr -dc '\$' | wc -m`
    if [[ "$count" -gt 2 ]]; then
        alg=`cat "$otisak" | cut -d'$' -f2`
        salt=`cat "$otisak" | cut -d"$" -f3`
    else
        alg="aixmd5"
        salt=`cat "$otisak" | cut -d'$' -f1`
    fi
    for ulaz in $ulazi; do
        sadrzaj=`cat "$ulaz"`
        hash=`openssl passwd -"$alg" -salt "$salt" "$sadrzaj"`
        if [[ "$hash" == "$otisakhash" ]]; then
            echo "$ulaz" : "$otisak"
        fi
    done
done

#ulaz14.txt : otisak19.txt
#ulaz8.txt : otisak2.txt
#ulaz4.txt : otisak7.txt
#ulaz15.txt : otisak8.txt
