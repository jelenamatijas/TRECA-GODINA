#!/bin/bash

confs=openssl*
otisak=`cat otisak.dec`

for conf in $confs; do
    hash=`openssl passwd -aixmd5 -salt "nije22" "$conf" 2>>error.log`
    if [[ "$hash" == "$otisak" ]]; then
        echo "$conf"
    fi
done

#openssl48.cnf
