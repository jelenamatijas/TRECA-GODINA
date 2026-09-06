#!/bin/bash
configs=openssl*
otisak=`cat otisak.dec`
for config in $configs; do
    hash=`openssl passwd -aixmd5 -salt nije22 "$config"`
    if [[ "$hash" == "$otisak" ]];then
    echo "$config"
fi
done

#openssl48
