#!/bin/bash
ulazi=ulaz*
for ulaz in $ulazi;do
    output=`openssl dgst -verify public.key -signature potpis.dec "$ulaz"`
    if [[ "$output" == "Verified OK" ]]; then
    echo "$ulaz"
fi
done

#ulaz33
