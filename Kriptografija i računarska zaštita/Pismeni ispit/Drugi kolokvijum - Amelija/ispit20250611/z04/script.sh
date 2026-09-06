#!/bin/bash
stores=store*
otisak=`cat sadrzajenv.key | openssl dgst -sha1 | cut -d' ' -f2`
for store in $stores; do
    openssl pkcs12 -in "$store" -nocerts -nodes -passin pass:sigurnost -passout pass:sigurnost -out private.key 2>>error.log
    hash=`openssl rsa -in private.key -pubout | openssl dgst -sha1 | cut -d' ' -f2`
    
    if [[ "$hash" == "$otisak" ]]; then
echo "$store"
fi
done

store50
