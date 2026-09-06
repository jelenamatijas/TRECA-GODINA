#!/bin/bash
stores=store*
keyhash=`openssl dgst -sha1 public.key  | cut -d' ' -f2`
for store in $stores; do
    hash=`openssl pkcs12 -in "$store" -nocerts -passin pass:sigurnost  -passout pass:sigurnost | openssl rsa -pubout -passin pass:sigurnost | openssl dgst -sha1 | cut -d' ' -f2`
    if [[ "$hash" == "$keyhash" ]]; then
echo "$store"
fi
done
    
#store44.p12
