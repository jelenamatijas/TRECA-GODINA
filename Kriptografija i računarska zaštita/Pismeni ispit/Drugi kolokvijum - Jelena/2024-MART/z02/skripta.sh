#!/bin/bash

otisci=otisak*.txt
for otisak in $otisci; do
    base64 -d "$otisak" > "${otisak%.txt}.dec"
done

openssl base64 -d -in sifrat.txt -out sifrat.dec

otisakdats=otisak*.dec
lozinke=lozinka*

for lozinka in $lozinke; do
    sadrzaj=`cat "$lozinka"`
    pass1=`openssl passwd -apr1 -salt lozinka1 "$sadrzaj"`
    pass2=`openssl passwd -apr1 -salt lozinka7 "$sadrzaj"`
    pass3=`openssl passwd -apr1 -salt wAgnh5WA "$sadrzaj"`
    for otisakdat in $otisakdats; do
        otisak=`cat "$otisakdat"`
        if [[ "$pass1" == "$otisak" ]]; then
            echo "prva : $lozinka"
        elif [[ "$pass2" == "$otisak" ]]; then
            echo "druga : $lozinka"
        elif [[ "$pass3" == "$otisak" ]]; then
            echo "treca : $lozinka"
        fi
    done
done


#prva : lozinka18.txt
#druga : lozinka3.txt
#treca : lozinka30.txt
algs=`openssl enc -list | grep aes`

for alg in $algs; do
    openssl enc "$alg" -d -pass pass:lozinka30  -in sifrat.dec -out tmp1.bin 2>>error.log
     openssl enc "$alg" -d -pass pass:lozinka3  -in tmp1.bin -out tmp2.bin 2>>error.log
     openssl enc "$alg" -d -pass pass:lozinka18  -in tmp2.bin -out tmp3.bin 2>>error.log
     echo "$alg: "
     cat tmp3.bin
     rm -f tmp1.bin tmp2.bin tmp3.bin
done

#es256:DANAS JE ISPIT IZ KRIPTOGRAFIJE
