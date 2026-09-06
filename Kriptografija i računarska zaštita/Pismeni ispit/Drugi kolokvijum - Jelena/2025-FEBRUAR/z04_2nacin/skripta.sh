#!/bin/bash


#keytool -list -keystore store.jks -storepass sigurnost

aliases="klijent10 klijent11 klijent12 klijent2 klijent3 klijent4 klijent5 klijent6 klijent7 klijent8 klijent9 server"

for alias in $aliases; do
    keytool -exportcert -alias "$alias" -keystore store.jks -rfc -file "$alias.crt" -storepass sigurnost
done

for cert in klijent*; do
    hash=`openssl x509 -in "$cert" -modulus -noout 2>error.log | openssl md5 | awk '{print $NF}'`
    for key in kljuc*; do
        hash2=`openssl rsa  -in "$key" -modulus -noout 2>error.log | openssl md5 | awk '{print $NF}'`
        if [[ "$hash" == "$hash2" ]]; then
            echo "$cert" : "$key"
        fi
    done
done

#   klijent10.crt : kljuc71.key
#klijent3.crt : kljuc17.key


