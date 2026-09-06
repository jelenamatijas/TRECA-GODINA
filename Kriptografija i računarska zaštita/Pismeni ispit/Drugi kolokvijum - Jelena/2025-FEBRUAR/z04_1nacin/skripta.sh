#!/bin/bash

keys=kljuc*
certs=cert*

for cert in $certs; do
    certhash=`openssl x509 -in "$cert" -modulus -noout 2>error.log | openssl md5 | awk '{print $NF}'`
    #echo "$certhash"
    for key in $keys; do
        hash=`openssl rsa -in "$key" -modulus -noout 2>error.log | openssl md5 | awk '{print $NF}'`
        #echo "$hash"
        if [[ "$hash" == "$certhash" ]]; then
            echo "$key"
        fi
    done
done

#kljuc17.key
#kljuc71.key

#komanda za razdvajanje sertifikata u posebne sertifikate: 
#csplit -z -f cert_ -b "%02d.crt" allcerts.crt '/-----BEGIN CERTIFICATE-----/' '{*}'
