#!/bin/bash
ulazi=ulaz*

for ulaz in $ulazi;do
    tekst=`openssl enc -d -in "$ulaz" -base64 `
    hash1=`openssl passwd -salt ETF -1 "$ulaz"`
    hash2=`openssl passwd -salt ETF -5 "$ulaz"`
    hash3=`openssl passwd -salt ETF -6  "$ulaz" `
    hash4=`openssl passwd -salt ETF -apr1 "$ulaz" `
    hash5=`openssl passwd -salt ETF -aixmd5 "$ulaz" `
    if [[ "$hash1" == "$tekst" ]]; then
echo "1: $ulaz"
 elif [[ "$hash2" == "$tekst" ]]; then
echo "5: $ulaz" 
 elif [[ "$hash3" == "$tekst" ]]; then
echo "6: $ulaz"
 elif [[ "$hash4" == "$tekst" ]]; then
echo "apr1:$ulaz"
 elif [[ "$hash5" == "$tekst" ]]; then
echo " aixmd5:$ulaz"
fi
done

ulaz40.txt -5
