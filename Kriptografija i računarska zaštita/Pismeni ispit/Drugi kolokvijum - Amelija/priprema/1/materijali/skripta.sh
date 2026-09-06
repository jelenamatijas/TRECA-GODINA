#!/bin/bash
keys=`ls ./ | grep "kljuc"`

openssl enc -base64 -d -in sifrat.txt -out sifrat.dec

for key in $keys
do
    openssl rsa -in $key -out $key.pem -inform DER -outform PEM 2>error1.txt
    rezultat=` openssl rsautl -decrypt -in sifrat.dec -inkey $key.pem 2>error2.txt`
    if [ "$rezultat "!= ""]
        then 
            echo -e "KLJUC: $key"
               echo -e "ULAZ: $rezultat"
    fi
done 
