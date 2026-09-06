#!/bin/bash

keys=kljuc*

mod1=`openssl rsa -pubin -in publicstore.key -noout -modulus | cut -d'=' -f2`

for key in $keys;do
     mod2=`openssl rsa -in "$key" -pubout -noout -modulus | cut -d'=' -f2`

    if [[ "$mod1" == "$mod2" ]];then
echo "$key"
fi
done

#ne radi
