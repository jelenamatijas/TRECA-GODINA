#!/bin/bash

lozinke=lozinka*
otisci=otisak*.dec

for lozinka in $lozinke; do
    sadrzaj=`cat "$lozinka"`
    h1=`openssl passwd -apr1 -salt lozinka1 $sadrzaj`
    h2=`openssl passwd -apr1 -salt lozinka7 $sadrzaj`
    h3=`openssl passwd -apr1 -salt wAgnh5WA $sadrzaj`
    
    for o in $otisci; do
        otisak=`cat $o`
        if [[ "$otisak" == "$h1" ]]; then
            echo "Prva: $lozinka"
        elif [[ "$otisak" == "$h2" ]]; then
            echo "Druga: $lozinka"
        elif [[ "$otisak" == "$h3" ]]; then
            echo "Treca: $lozinka"
        fi
    done
done
        
#Prva: lozinka18.txt
#Treca: lozinka30.txt
#Druga: lozinka3.txt

algs=`openssl enc -list | grep aes`

for alg in $algs;do
echo "algoritam: $alg "
    openssl enc -d "$alg" -in sifrat.dec -out t1.bin -pass pass:lozinka30 
    openssl enc -d "$alg" -in t1.bin -out t2.bin -pass pass:lozinka3
    openssl enc -d "$alg" -in t2.bin -out t3.bin -pass pass:lozinka18
    cat t3.bin
done
