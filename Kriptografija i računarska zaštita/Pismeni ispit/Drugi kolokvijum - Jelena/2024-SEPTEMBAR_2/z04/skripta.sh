#!/bin/bash

openssl pkcs12 -in cert.p12 -cacerts -nokeys -out ca.crt -passin pass:sigurnost
openssl x509 -in ca.crt -pubkey -noout > key.key 2>/dev/null
ulazi=ulaz*

for ulaz in $ulazi; do
    output=`openssl dgst -verify key.key -signature "potpis.dec" "$ulaz" 2>error.log` 
    if [[ "$output" == "Verified OK" ]]; then
        echo "$ulaz"
    fi
done

#ulaz33.txt
