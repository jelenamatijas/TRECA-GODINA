#!/bin/bash

confs=openssl*
otisak=`cat otisak.dec`

for conf in $confs; do
	h=`openssl passwd -aixmd5 -salt nije22 "$conf"`
    if [[ "$otisak" == "$h" ]]; then
        echo "$conf"
    fi
done

#openssl48.cnf
