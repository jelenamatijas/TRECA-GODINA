#!/bin/bash

ulazi=ulaz*

for ulaz in $ulazi; do
        output=`openssl dgst -prverify kljuc.key -signature potpis.dec -passin pass:sigurnost -sha1 "$ulaz" 2>>error.log`
    if [[ "$output" == "Verified OK" ]]; then 
    echo "$ulaz"
fi
 done

algs=`openssl enc -list | grep "^-des"`
for alg in $algs; do
    decrypt=`openssl enc -provider legacy -provider default -d  -in sifrat.dec -pass pass:ulaz73.txt 2>>error.log`
    echo "$alg : $decrypt"
done
#ulaz73.txt

#ne moze se desifrovati??
