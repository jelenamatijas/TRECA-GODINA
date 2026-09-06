#!/bin/bash
certs=client*
ca=`openssl x509 -in cacert.pem -subject -noout | sed "s/subject//" ` 

for cert in $certs;do

    issuer=`openssl x509 -in "$cert" -issuer -noout | sed "s/issuer//"`

if [[ "$issuer" == "$ca" ]]; then
echo "$cert"
fi
done

# 10 12 16 17 1 23 30 3 4 8
