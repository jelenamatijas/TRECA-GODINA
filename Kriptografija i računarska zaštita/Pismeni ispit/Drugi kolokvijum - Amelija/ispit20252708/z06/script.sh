#!/bin/bash

keys=kljuc*
otisak=`cat otisak.dec `

for key in $keys; do
    hash=`openssl dgst -sha1 "$key" | cut -d' ' -f2`
 
    if [[ "$hash" == "$otisak" ]];then
echo "$key"
fi
done
    
kljuc43
