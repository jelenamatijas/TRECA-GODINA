#!/bin/bash

keys=kljuc*
mod1=`openssl rsa -in cert.key -modulus -noout`
for key in $keys;do
    mod2=`openssl rsa -in "$key" -modulus -noout`

    if [[ "$mod1" == "$mod2" ]]; then
echo "$key"
fi
done

#kljuc50.key
