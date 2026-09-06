#!/bin/bash
ulazi=ulaz*
otisak2=`cat "otisak.txt" | tr -d '\n'`

for ulaz in $ulazi; do
    otisak1=`openssl dgst -shake256 "$ulaz" | awk '{print $2}'  `

    if [[ "$otisak1" == "$otisak2" ]]; then
    crypt1=`openssl enc -d -camellia-192-ecb -in sifrat.dec -pass pass:"$ulaz" `
    crypt2=`openssl enc -d -camellia-192-cbc -in sifrat.dec -pass pass:"$ulaz"`
    crypt3=`openssl enc -d -camellia-128-ecb -in sifrat.dec -pass pass:"$ulaz" `
    crypt4=`openssl enc -d -camellia-128-cbc -in sifrat.dec -pass pass:"$ulaz"  `
    crypt5=`openssl enc -d -camellia-256-ecb -in sifrat.dec -pass pass:"$ulaz"`
    crypt6=`openssl enc -d -camellia-256-cbc -in sifrat.dec -pass pass:"$ulaz"`
    original=`cat "$ulaz"`
 if [[ "$crypt1" == "$original" ]]; then
    echo $ulaz
    fi
 if [[ "$crypt2" == "$original" ]]; then
    echo $ulaz
    fi
 if [[ "$crypt3" == "$original" ]]; then
    echo $ulaz
    fi
 if [[ "$crypt4" == "$original" ]]; then
    echo $ulaz
    fi
 if [[ "$crypt5" == "$sifrat" ]]; then
    echo $ulaz
    fi
 if [[ "$crypt6" == "$sifrat" ]]; then
    echo $ulaz
    fi
fi
done
    
    
