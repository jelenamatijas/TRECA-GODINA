#!/bin/bash

keys=kljuc*

hash=`openssl rsa -pubin -in "public.key" -modulus -noout 2>error.log | openssl md5 | awk '{printf $NF}'`
echo "$hash"

for key in $keys; do
    keyhash=`openssl rsa -in "$key" -modulus -noout 2>error.log | openssl md5 | awk '{printf $NF}'`
    #echo "$key": "$keyhash"
    if [[ "$keyhash" == "$hash" ]]; then
        echo "$key"
    fi

keyhash=`openssl rsa -pubin -in "$key" -modulus -noout 2>error.log | openssl md5 | awk '{printf $NF}'`
    #echo "$key": "$keyhash"
    if [[ "$keyhash" == "$hash" ]]; then
        echo "$key"
    fi
done

#kljuc50.key
