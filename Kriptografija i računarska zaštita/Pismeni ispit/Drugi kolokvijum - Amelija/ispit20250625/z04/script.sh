#!/bin/bash

stores=store*

for store in $stores; do
    openssl pkcs12 -nodes -nocerts -in "$store" -out tmp.dat -passin pass:sigurnost -passout pass:sigurnost 2>>error.log
    output=`openssl rsautl -decrypt -in envelopa.dec -inkey tmp.dat 2>>error.log`
    echo "$store : $output"
done    
       
store13
