#!/bin/bash
lozinke=lozinka*

    otisak1=`cat otisak1.dec | tr -d ' \r\n'`
    otisak2=`cat otisak2.dec | tr -d ' \r\n'`
    otisak3=`cat otisak3.dec | tr -d ' \r\n'`

for lozinka in $lozinke; do
    sadrzaj=`cat "$lozinka"`
    hash1=`openssl passwd -apr1 -salt lozinka1 $sadrzaj `
    hash2=`openssl passwd -apr1 -salt lozinka7 $sadrzaj`
    hash3=`openssl passwd -apr1 -salt wAgnh5WA $sadrzaj`

    if [[ "$hash1" == "$otisak1" ]]; then
    echo " prva lozinka : "$lozinka""
     kljuc1="$sadrzaj"
elif [[ "$hash2" == "$otisak2" ]]; then
    echo " druga lozinka : "$lozinka""
 kljuc2="$sadrzaj"
elif [[ "$hash3" == "$otisak3" ]]; then
    echo " treca lozinka : "$lozinka""
 kljuc3="$sadrzaj"
fi
done

 algs=`openssl enc -list | grep "^-aes-256"`

for alg in $algs; do
echo "$alg"
    openssl enc -d "$alg" -nosalt -in sifrat.dec -out tmp1.bin -pass pass:"$kljuc3" 2>>error.log 
     openssl enc -d "$alg" -nosalt -in tmp1.bin -out tmp2.bin -pass pass:"$kljuc1" 2>>error.log
 openssl enc -d "$alg" -nosalt -in tmp2.bin -out tmp3.bin -pass pass:"$kljuc2" 2>>error.log
    
    echo "sadrzaj:"
cat tmp3.bin
done
#prva lozinka lozinka18
#druga lozinka lozinka3
#treca lozinka lozinka30

ne mogu desifrovati
