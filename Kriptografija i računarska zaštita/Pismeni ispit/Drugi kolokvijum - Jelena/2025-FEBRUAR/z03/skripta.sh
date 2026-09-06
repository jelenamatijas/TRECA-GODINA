#!/bin/bash

keys=kljuc*

for key in $keys; do
    rez=`openssl rsautl -decrypt -in sifrat.dec -inkey "$key" -passin pass:sigurnost 2>error.log`
    echo "$key":"$rez"
done

#kljuc85.key:OSVOJILI STE 1 BOD. IDEMO NA SLEDECI ZADATAK.
