#!/bin/bash
ulazi=ulaz*

for ulaz in $ulazi;do
    izlaz=`openssl dgst -verify public.key -signature potpis.txt "$ulaz" 2>>error.log` 
    if [[ "$izlaz" == "Verified OK" ]]; then echo " $ulaz"
fi
    done

ulaz25.txt
