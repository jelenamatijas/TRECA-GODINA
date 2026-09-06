#!/bin/bash

certs=client*
m=`openssl rsa -in client.key -modulus -noout`
for cert in $certs; do
    if openssl verify -CAfile ca.crt "$cert" 2>error.log | grep -q "OK"; then
        m1=`openssl x509 -in "$cert" -modulus -noout`
        if [[ "$m1" == "$m" ]]; then
            echo "$cert"
            openssl pkcs12 -export -inkey client.key -certfile ca.crt -in "$cert" -out "klijent.p12" -passout pass:sigurnost
        fi
    fi
done

#client24.crt

