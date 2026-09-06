#!/bin/bash

ulazi=ulaz*
otisak=`cat kontrolni_hash.txt`
for ulaz in $ulazi;do
    hash=`openssl dgst -sha3-512 "$ulaz" | cut -d' ' -f2`
    if [[ "$hash" == "$otisak" ]] ;then 
echo " pronadjena $ulaz"
    key=`openssl dgst -sha3-256 "$ulaz" | cut -d' ' -f2`
    echo "$key"
    dekriptovani=`openssl enc -d -aes-256-ecb -in "sifrat.txt" -pass pass:"$key"`
    echo "Dekriptovani sifrat: $dekriptovani a ulaz: "
    cat "$ulaz"
fi
done

ulaz37.txt
