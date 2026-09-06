#!/bin/bash

algs=`openssl enc -list | awk '{print $1}' | grep "^-aes" | cut -c 2-`
ulazi=ulaz*

for alg in $algs; do
    temp=`mktemp`
    openssl enc -d -in sifrat.dec -$alg -pass pass:"$alg" -out "$temp" 2>>error.log
    for ulaz in $ulazi; do
        if cmp -s "$temp" "$ulaz"; then
            echo "$alg" : "$ulaz"
        fi    
    done   
    rm -f "$temp" 
done

# nije rjesenje zadatka: nije moguce dekriptovati sifrat
        
