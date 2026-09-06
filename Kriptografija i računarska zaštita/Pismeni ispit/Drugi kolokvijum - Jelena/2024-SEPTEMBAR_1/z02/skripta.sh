#!/bin/bash

ulazi=ulaz*

for ulaz in $ulazi; do
    salt=$(tr -d '\r\n' < "$ulaz")
    
    otisak=$(openssl passwd -5 -salt "$salt" "$ulaz" 2>>error.log)
    
    for opt in "" "-a" "-nosalt"; do
        openssl enc -d -aria-192-ofb $opt -in "sifrat.dec" -out "dekriptovano.txt" -pass pass:"$otisak" 2>>error.log
        echo "$ulaz"
        cat "dekriptovano.txt"
    done
done

#ulaz28.txt
#ULAZNI SADRZAJ JE TU, SAMO GA TREBA DEKRIPTOVATI.
