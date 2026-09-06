#!/bin/bash

ulazi=ulaz*
otisak=`cat otisak.dec`
for ulaz in $ulazi; do
    hash=`openssl passwd -5 -salt ulaz14.txt "$ulaz"`
    if [[ "$hash" == "$otisak" ]];then
echo "$ulaz"
fi
done
ulaz35
