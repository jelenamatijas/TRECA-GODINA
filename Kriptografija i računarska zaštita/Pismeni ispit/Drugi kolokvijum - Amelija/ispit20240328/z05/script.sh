#!/bin/bash

dats=openssl*
otisak=`cat otisak.dec`
for dat in $dats; do
    hash=`openssl passwd -aixmd5 -salt nije22 "$dat"`
    if [[ "$otisak" == "$hash" ]];then
    echo "$dat"
fi
done

#openssl48.cnf
