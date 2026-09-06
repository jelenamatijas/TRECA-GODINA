#!/bin/bash
keys=kljuc*
mod1=`openssl x509 -in klijent.crt -modulus -nocert -noout`
for key in $keys;do
    mod2=`openssl rsa -in "$key" -modulus -noout`
 
    if [[ "$mod1" == "$mod2" ]]; then
    echo "$key"
fi
done

openssl pkcs12 -export -out klijent.p12 -inkey kljuc17.key -in klijent.crt -certfile ca.pem

#kljuc17
