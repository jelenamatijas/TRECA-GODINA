#!/bin/bash

ulazi=ulaz*

for ulaz in $ulazi; do
    izlaz=`openssl dgst -sha256 -verify public.key -signature potpis.dec "$ulaz" 2>error.log `
    if [[ "$izlaz" == "Verified OK" ]]; then
       echo " "$ulaz" odgovara"
    fi
    done 

