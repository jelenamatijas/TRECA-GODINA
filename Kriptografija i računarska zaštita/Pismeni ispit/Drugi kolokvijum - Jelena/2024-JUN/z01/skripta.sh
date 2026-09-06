#!/bin/bash

files=ulaz*
otisak=`cat otisak.dec`

for file in $files; do
    rez=`openssl passwd -5 -salt ulaz12.txt "$file"`
    if [[ "$otisak" == "$rez" ]]; then
        echo "$file"
    fi
done

#ulaz10.txt
