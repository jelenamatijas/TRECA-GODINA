#!/bin/bash
otisci=otisak*
ulazi=ulaz*

for otisak in $otisci;do
    sadrzajot=`cat "$otisak" | tr -d '\n\r '`
    count=`tr -dc "$" <"$otisak" | wc -c`
  
    if [[ "$count" -gt 1 ]]; then
    salt=`cat "$otisak" | awk -F"$" '{print $3}'`

    alg=`cat "$otisak" | awk -F"$" '{print $2}'`
    else
     salt=`cat "$otisak" | awk -F"$" '{print $1}'`

    alg="aixmd5"
    fi
    for ulaz in $ulazi; do
     hash=`openssl passwd -"$alg" -salt "$salt" -in "$ulaz"`
    if [[ "$hash" == "$sadrzajot" ]]; then
    echo " "$otisak" : "$ulaz" "
    fi
done
done
rjesen
