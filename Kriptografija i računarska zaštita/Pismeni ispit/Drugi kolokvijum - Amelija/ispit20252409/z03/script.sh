#!/bin/bash

ulazi=ulaz*

for ulaz in $ulazi; do
    sadrzaj=`openssl enc -d -in "$ulaz" -base64`
    hash1=`openssl passwd -1 -salt ETF "$ulaz"`
hash2=`openssl passwd -5 -salt ETF "$ulaz"`
hash3=`openssl passwd -6 -salt ETF "$ulaz"`
hash4=`openssl passwd -apr1 -salt ETF "$ulaz"`
hash5=`openssl passwd -aixmd5 -salt ETF "$ulaz"`
    
if [[ "$hash1" == "$sadrzaj" ]]; then echo "1: $ulaz"
elif [[ "$hash2" == "$sadrzaj" ]]; then echo "5: $ulaz"
elif [[ "$hash3" == "$sadrzaj" ]]; then echo "6: $ulaz"
elif [[ "$hash4" == "$sadrzaj" ]]; then echo "apr1: $ulaz"
elif [[ "$hash5" == "$sadrzaj" ]]; then echo "aixmd5: $ulaz"
fi
done
    
5:ulaz40.txt
