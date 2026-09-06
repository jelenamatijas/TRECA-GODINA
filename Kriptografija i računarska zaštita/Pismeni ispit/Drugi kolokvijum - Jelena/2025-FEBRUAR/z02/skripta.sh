#!/bin/bash

otisak=`cat otisak.dec`
ulazi=ulaz*

for ulaz in $ulazi; do
    rez=`openssl passwd -5 -salt "ulaz14.txt" "$ulaz"`
    #echo "$rez"
    if [[ "$rez" == "$otisak" ]]; then
        echo "$ulaz"
    fi
done

#ulaz35.txt
