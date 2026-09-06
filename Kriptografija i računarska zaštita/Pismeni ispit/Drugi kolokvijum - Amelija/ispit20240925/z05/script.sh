#!/bin/bash

certs=cert*

for cert in $certs;do
    text=`openssl x509 -in "$cert" -noout -c`
echo "$text"
    if echo "$text" | grep -q 'sha224' ; then
    echo "$cert"
fi
done

openssl s_client -connect HOST:PORT -servername HOST -showcerts </dev/null 2>/dev/null \
| openssl x509 -out server_tls.crt # ovo je nacin da se dobije sertifikat trazeni sa sajta
