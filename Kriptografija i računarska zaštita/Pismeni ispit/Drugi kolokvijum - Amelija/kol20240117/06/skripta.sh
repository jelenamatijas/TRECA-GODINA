#!/bin/bash
stores=store*

for store in $stores; do
    openssl pkcs12 -in "$store" -nokeys -out cert.pem -passin pass:sigurnost
    openssl x509 -in cert.pem -text -noout > output.txt
    if grep -q "TLS Web Server Authentication" output.txt && grep -q "TLS Web Client Authentication" output.txt; then
    echo " $store"
break
fi
done
