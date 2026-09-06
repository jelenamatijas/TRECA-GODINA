#!/bin/bash
ulazi=ulaz*
otisak=`cat otisak.dec`
echo "$otisak"
for ulaz in $ulazi; do
    hash=`openssl dgst -sha256 "$ulaz" | cut -d' ' -f2`
    echo "$hash"
    if [[ "$otisak" == "$hash" ]]; then
    echo "$ulaz"
fi
done

#ulaz13
#izracunati ostatak
