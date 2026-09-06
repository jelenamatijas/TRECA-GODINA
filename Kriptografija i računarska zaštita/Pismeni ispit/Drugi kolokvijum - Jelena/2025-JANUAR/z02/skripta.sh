#!/bin/bash

lozinke=lozinka*

algs=`openssl dgst -list | grep sha`
otisak=`cat otisak.txt`
for lozinka in $lozinke; do
    for alg in $algs; do
        hash=`openssl dgst "$alg" "$lozinka" | awk '{printf $NF}'`
        #echo "$hash"
        if [[ "$hash" == "$otisak" ]] ; then
            echo "$alg" : "$lozinka"
        fi
    done
done

#-sha512 : lozinka19.txt

desalgs=`openssl enc -list | grep des`

for da in $desalgs; do
    openssl enc -d "$da" -pass pass:"lozinka19" -in "sifrat.dec" -out "temp1.bin" 2>error.log
    openssl enc -d "$da" -pass pass:"lozinka19" -in "temp1.bin" -out "temp2.bin" 2>error.log
    openssl enc -d "$da" -pass pass:"lozinka19" -in "temp2.bin" -out "temp3.bin" 2>error.log
    echo "$da": 
    cat "temp3.bin"
done

#-des-ede3-ofb: OSVOJILI STE 1 BOD. IDEMO NA SLEDECI ZADATAK.
