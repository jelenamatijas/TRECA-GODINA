#!/bin/bash

confs=*.cnf
otisak=`cat otisak.dec`

for conf in $confs; do
    hash=`openssl passwd -aixmd5 -salt nije22 $conf`
    if [[ "$otisak" == "$hash" ]]; then
        echo "$conf"
    fi
done

#openssl48.cnf
