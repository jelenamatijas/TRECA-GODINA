#!/bin/bash
keys=kljuc*

for key in $keys; do
    decrypt=`openssl rsautl -decrypt -in sifrat.dec -inkey "$key" 2>>error.log`
    echo "$key : $decrypt"
done

kljuc85
