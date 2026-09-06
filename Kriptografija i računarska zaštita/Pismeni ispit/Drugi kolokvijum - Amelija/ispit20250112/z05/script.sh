#!/bin/bash
stores=store*

for store in $stores;
do
    text=`openssl x509 -in "$store" -noout -text -passin pass:sigurnost`
if echo "$text" | grep -q "TLS Web Client Authentication" && echo "$text" | grep -q "TLS Web Server Authentication" ;then
    echo "$store"
fi
done

#store27.p12
