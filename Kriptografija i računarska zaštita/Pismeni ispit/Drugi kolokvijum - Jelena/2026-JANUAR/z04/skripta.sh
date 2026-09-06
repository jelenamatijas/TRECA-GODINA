#!/bin/bash

potpisi=signature*

sadrzaj=`cat ulaz.txt`

for potpis in $potpisi; do
    rez=`openssl dgst -sha-256 -prverify privatni_kljuc.pem -signature "$potpis" ulaz.txt 2>error.log`
    if [[ "$rez" == "Verified OK" ]]; then
        echo "$potpis"
    fi
done 

#signature75.txt
