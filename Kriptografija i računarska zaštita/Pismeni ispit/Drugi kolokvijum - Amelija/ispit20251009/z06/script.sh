#!/bin/bash
keys=kljuc*

otisak=`cat otisak.dec | tr -d '\r\n '`
echo "$otisak"
for key in $keys; do
    
    hash=` openssl dgst -sha1 "$key" | cut -d' ' -f2`
echo "$hash"    
if [[ "$hash" == "$otisak" ]];then 
echo " pronadjen $key"
fi
done
    
pronadjen kljuc43.key
