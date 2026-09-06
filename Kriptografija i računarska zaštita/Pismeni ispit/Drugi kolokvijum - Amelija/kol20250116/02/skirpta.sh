#!/bin/bash
stores=store*
envelopa="envelopa.dec"
passfile="pass.txt"

for store in $stores;
    do
        openssl pkcs12 -in "$store" -nocerts -nodes -passin file:"$passfile" -out key.pem 2>/dev/null 
       if openssl rsautl -decrypt -inkey key.pem -in "$envelopa" -out sadrzaj.txt 2>/dev/null; then 
        echo "store $store "
        cat sadrzaj.txt
      fi
done 

        

