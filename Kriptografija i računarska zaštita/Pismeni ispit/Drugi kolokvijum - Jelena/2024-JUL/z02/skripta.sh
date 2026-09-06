#!/bin/bash

otisci=otisak*.dec
lozinke=lozinka*

for l in $lozinke; do
    lozinka=`cat $l`;
    h1=`openssl passwd -apr1 -salt lozinka1 "$lozinka"`
    h2=`openssl passwd -apr1 -salt lozinka7 "$lozinka"`
    h3=`openssl passwd -apr1 -salt wAgnh5WA "$lozinka"`

    for o in $otisci; do
        otisak=`cat $o`;
        if [[ "$otisak" == "$h1" ]]; then
            echo "prva: $lozinka"
        elif [[ "$otisak" == "$h2" ]]; then
            echo "druga: $lozinka"
        elif [[ "$otisak" == "$h3" ]]; then
            echo "treca: $lozinka"
        fi
    done
done

#prva: lozinka18
#treca: lozinka30
#druga: lozinka3

algs=`openssl enc -list | grep aes`

for alg in $algs; do
    openssl enc "$alg" -d -pass pass:lozinka30 -in sifrat.dec -out temp1.bin 2>>error.log
    openssl enc "$alg" -d -pass pass:lozinka3 -in temp1.bin -out temp2.bin 2>>error.log
    openssl enc "$alg" -d -pass pass:lozinka18 -in temp2.bin -out temp3.bin 2>>error.log
    cat temp3.bin
    echo " "
done

#DANAS JE ISPIT IZ KRIPTOGRAFIJE 
