#!/bin/bash

potpisi=signature*

for potpis in $potpisi; do
    output=`openssl dgst -verify public.key -signature "$potpis" ulaz.txt 2>>error.log`
    if [[ "$output" == "Verified OK" ]];then
    echo "$potpis"
fi
    done


signature90.txt
