#!/bin/bash

ulazi=ulaz*
algs="-1 -5 -6"
for ulaz in $ulazi; do
    sadrzaj=`cat "$ulaz" | openssl enc -d -base64`
    count=`echo "$sadrzaj" | tr -dc '\$' | wc -m`
    if [[ "$count" -gt 1 ]]; then
        alg=`echo "$sadrzaj" | cut -d'$' -f2`
        hash=`openssl passwd -"$alg" -salt ETF "$ulaz"`
    else
        hash=`openssl passwd -aixmd5 "$ulaz"`
    fi
    if [[ "$hash" == "$sadrzaj" ]]; then
            echo "$ulaz"
    fi
done

#ulaz40.txt
