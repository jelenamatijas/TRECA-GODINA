#!/bin/bash

keys=kljuc*
otisak=`openssl dgst -sha1 klijent.pub | cut -d' ' -f2`
for key in $keys;do
    hash=`openssl rsa -in "$key" -pubout | openssl dgst -sha1 | cut -d' ' -f2`
    if [[ "$hash" == "$otisak" ]]; then
    echo "$key"
fi
done

kljuc26
