#!/bin/bash

otisakdats=otisak*.dec
lozinke=lozinka*

for lozinka in $lozinke; do
    sadrzaj=`cat "$lozinka"`
    pass1=`openssl passwd -apr1 -salt lozinka1 $sadrzaj`
    pass2=`openssl passwd -apr1 -salt lozinka7 $sadrzaj`
    pass3=`openssl passwd -apr1 -salt wAgnh5WA $sadrzaj`
    for otisakdat in $otisakdats;do
    otisak=`cat "$otisakdat"`
    if [[ "$pass1" == "$otisak" ]];then
    echo "prva : $lozinka"
 elif [[ "$pass2" == "$otisak" ]];then
    echo "druga : $lozinka"
 elif [[ "$pass3" == "$otisak" ]];then
    echo "treca : $lozinka"
fi

done
done

#1 lozinka18
#2 lozinka3
#lozinka30

algs=`openssl enc -list | grep aes`

for alg in $algs; do
    openssl enc "$alg" -d -pass pass:lozinka30  -in sifrat.dec -out tmp1.bin 2>>error.log
     openssl enc "$alg" -d -pass pass:lozinka3  -in tmp1.bin -out tmp2.bin 2>>error.log
 openssl enc "$alg" -d -pass pass:lozinka18  -in tmp2.bin -out tmp3.bin 2>>error.log
    echo "$alg: "
cat tmp3.bin

rm -f tmp1.bin tmp2.bin tmp3.bin
done

#aes256 DANAS JE ISPIT IZ KRIPOTOGRAFIJE
