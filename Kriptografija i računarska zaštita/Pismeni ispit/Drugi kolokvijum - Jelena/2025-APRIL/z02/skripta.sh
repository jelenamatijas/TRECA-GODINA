#!/bin/bash

otisci=otisak*.dec

lozinke=lozinka*

for otisak in $otisci; do
    salt=`cat "$otisak" | cut -d'$' -f3`
    sadrzaj=`cat "$otisak" | tr -d ' \n\r'`
    for lozinka in $lozinke; do
        text=`cat "$lozinka" | tr -d ' \n\r'`
        rez=`openssl passwd -apr1 -salt "$salt" "$text"`
        if [[ "$rez" ==  "$sadrzaj" ]]; then
            echo "$otisak": "$lozinka"
        fi   
    done
done

#otisak1.dec: lozinka18.txt
#otisak2.dec: lozinka3.txt
#otisak3.dec: lozinka30.txt

lozinka1=`cat lozinka18.txt | tr -d ' \n\r'`
lozinka2=`cat lozinka3.txt | tr -d ' \n\r'`
lozinka3=`cat lozinka30.txt | tr -d ' \n\r'`


algs=`openssl enc -list | grep ^-aes-256`

for alg in $algs; do
    openssl enc -d "$alg" -pass pass:$lozinka3 -in sifrat.dec -out temp1.bin 2>error.log
    openssl enc -d "$alg" -pass pass:$lozinka2 -in temp1.bin -out temp2.bin 2>error.log
    openssl enc -d "$alg" -pass pass:$lozinka1 -in temp2.bin -out temp3.bin 2>error.log
    echo "$alg : "
    cat "temp3.bin"
done

# pogresni materijali, nije moguce desifrovati
