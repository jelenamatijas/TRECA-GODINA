#!/bin/bash

ulazi=ulaz*

for ulaz in $ulazi; do
 output=`openssl dgst -verify public.key -signature potpis.txt "$ulaz" 2>>error.log`
if [[ "$output" == "Verified OK" ]]; then
echo "$ulaz"
fi
done
ulaz33
