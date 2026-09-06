#!/bin/bash

keys=kljuc*
mod1=`openssl x509 -in klijent.crt -modulus -nocert`
for key in $keys;do
    mod2=`openssl rsa -in "$key" -modulus -noout`
    if [[ "$mod1" == "$mod2" ]]; then
    echo "$key"
fi
done

#kljuc42.key
