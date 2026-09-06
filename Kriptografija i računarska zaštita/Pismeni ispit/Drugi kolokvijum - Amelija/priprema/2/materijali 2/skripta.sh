#!/bin/bash

ciphers="izlaz*"

# dekodujemo otisak iz base64 u hex oblik
openssl enc -in otisak.hash -out otisak.dec -base64 -d

# ucitamo sadrzaj datoteke u promjenljivu
otisak1=$(<otisak.dec)

for cipher in $ciphers
do
    # racunamo SHA224 otisak datoteke sa sifratom i odbacujemo prefiks "SHA2-224(otisak.txt)= "
    otisak2=`openssl dgst -sha224 $cipher | cut -d ' ' -f 2`
    if [[ "$otisak1" == "$otisak2" ]]
    then
        echo $cipher
        openssl aes-256-cbc -in $cipher -out result -k sigurnost -d 2>error.txt
        cat result
    fi
done 



# izlaz71.crypt
# Ulazna datoteka za prvi zadatak_

