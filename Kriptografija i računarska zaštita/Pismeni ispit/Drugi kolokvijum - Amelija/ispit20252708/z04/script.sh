#!/bin/bash

keys=private*
sifrati=envelope*

for sifrat in $sifrati; do
    echo " Isprobavamo kljuceve za "$sifrat" : "
    for key in $keys;do
    openssl rsautl -in "$sifrat" -inkey "$key" -decrypt -out desifrovano.bin 2>>error.log
  if LC_ALL=C grep -qP '^[\x09\x0A\x0D\x20-\x7E]+$' desifrovano.bin; then
    echo " sifrat : $sifrat odgovara kljuc: $key "
    echo "sadraj:"
cat desifrovano.bin


fi
done 
done

env10 priv40
