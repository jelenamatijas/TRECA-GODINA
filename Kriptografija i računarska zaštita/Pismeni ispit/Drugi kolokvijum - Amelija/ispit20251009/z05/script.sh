#!/bin/bash
keys=kljuc*
hash_k=`openssl dgst -sha256 klijent.key | cut -d' ' -f2 `

for key in $keys;do
    openssl rsa -in "$key" -pubout -out kljuc.pub 
    hash=`openssl dgst -sha256 kljuc.pub | cut -d' ' -f2`
    
    if [[ "$hash" == "$hash_k" ]];then
echo "$key" 
fi
done
    
kljuc28.key
