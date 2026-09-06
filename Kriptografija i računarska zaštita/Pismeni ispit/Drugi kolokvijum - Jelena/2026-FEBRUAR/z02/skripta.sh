#!/bin/bash

lozinke=lozinka*
otisci=otisak*

algs=`openssl dgst -list | grep sha`


for otisak in $otisci; do
    hash=`cat "$otisak"`
    for lozinka in $lozinke; do
        for alg in $algs; do
            rez=`openssl dgst "$alg" "$lozinka" | cut -d' ' -f2`
            if [[ "$rez" == "$hash" ]]; then
                echo "$otisak" : "$lozinka" : "$alg"
            fi
        done
    done
done

#otisak1.txt : lozinka37.txt : -sha384
#otisak2.txt : lozinka25.txt : -sha224
#otisak3.txt : lozinka36.txt : -sha256

pass1=`cat lozinka37.txt`
pass2=`cat lozinka25.txt`
pass3=`cat lozinka36.txt`

openssl enc -aria-256-ofb -d -in sifrat.dec -pass pass:$pass3 -out temp1.bin 2>error.log
openssl enc -aria-256-ofb -d -in temp1.bin -pass pass:$pass2 -out temp2.bin 2>error.log
openssl enc -aria-256-ofb -d -in temp2.bin -pass pass:$pass1 -out temp3.bin 2>error.log

cat temp3.bin

#Ako ovo vidite, kopirajte ovaj tekst u skriptu i pravac na sljedeci zadatak ;-)!
