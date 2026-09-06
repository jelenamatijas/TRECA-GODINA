#!/bin/bash

ulazi=ulaz*
algs=`openssl enc -list | awk '{print $1}' | grep "^-aes" | cut -c 2-`
for ulaz in $ulazi; do
    for alg in $algs; do
    tmp=`mktemp`
    openssl enc -d  -in sifrat.dec -$alg -pass pass:"$alg" -out "$tmp" 2>>error.log
   cat "$tmp"
    if cmp -s "$tmp" "$ulaz"; then
    echo " $alg : $ulaz"
fi
    rm -f "$tmp"
done
done

#ne mogu dekriptovati??
