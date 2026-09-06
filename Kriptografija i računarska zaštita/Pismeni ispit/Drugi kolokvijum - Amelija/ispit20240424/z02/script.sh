#!/bin/bash
lozinke=lozinka*
otisci=otisak*
for lozinka in $lozinke;do
    ulaz=`cat "$lozinka"`
    hash1=`openssl passwd -apr1 -salt lozinka1 "$ulaz"`
    hash2=`openssl passwd -apr1 -salt lozinka7 "$ulaz"`
    hash3=`openssl passwd -apr1 -salt wAgnh5WA "$ulaz"`
    for otisak in $otisci; do
    otisakhash=`cat "$otisak"`
    if [[ "$hash1" == "$otisakhash" ]]; then
    echo " $otisak : $lozinka"
    elif [[ "$hash2" == "$otisakhash" ]]; then
    echo " $otisak : $lozinka"
    elif [[ "$hash3" == "$otisakhash" ]]; then
    echo " $otisak : $lozinka"
fi
done
done

algs=`openssl enc -list | grep "^-aes-256"`

for alg in $algs;do
echo "algoritam: $alg "
    openssl enc -d "$alg" -in sifrat.dec -nosalt -out t1.bin -pass pass:lozinka30 
    openssl enc -d "$alg" -in t1.bin -nosalt -out t2.bin -pass pass:lozinka3
    openssl enc -d "$alg" -in t2.bin -nosalt -out t3.bin -pass pass:lozinka18
    cat t3.bin
done
#otisak1 lozinka18
#otisak3 lozinka30
#otisak2 lozinka3

#NE MOZE DEKRIPTOVATI !!!!!!
