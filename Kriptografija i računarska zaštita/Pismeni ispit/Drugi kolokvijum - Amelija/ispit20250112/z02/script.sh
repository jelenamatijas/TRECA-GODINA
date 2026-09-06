#!/bin/bash
lozinke=lozinka*
otisak=`cat otisak.txt`
for lozinka in $lozinke;do
hash=`openssl dgst -sha512 "$lozinka" | cut -d' ' -f2`
    if [[ "$hash" == "$otisak" ]]; then
echo "$lozinka"
    key=$lozinka
fi
done

algs=`openssl enc -list | tr ' ' '\n' | grep "^-des"`
for alg in $algs;do
    echo "$alg"
  openssl enc -legacy -d $alg -salt -in sifrat.dec -out tmp.bin -pass file:"$lozinka" 2>>error.log 
openssl enc -d $alg -legacy -in tmp.bin -out tmp2.bin -salt -pass file:"$lozinka" 2>>error.log 
openssl enc -d $alg -legacy -in tmp2.bin -out tmp3.bin -salt -pass file:"$lozinka" 2>>error.log 
    echo " sadrzaj za $alg : "
cat tmp3.bin
rm -f tmp.bin tmp2.bin tmp3.bin
done

#lozinka19.txt
#ne moze dekriptovati iz nekog razloga
