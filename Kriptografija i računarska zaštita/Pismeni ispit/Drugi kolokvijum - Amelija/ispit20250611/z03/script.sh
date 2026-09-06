#!/bin/bash
ulazi=ulaz*

for ulaz in $ulazi; do
    openssl enc -d -base64 -in "$ulaz" -out izlaz.tmp
    hash1=`openssl passwd -salt ETF -1 "$ulaz"`
    hash2=`openssl passwd -salt ETF -5 "$ulaz"`
hash3=`openssl passwd -salt ETF -6 "$ulaz"`
hash4=`openssl passwd -salt ETF -apr1 "$ulaz"`
hash5=`openssl passwd -salt ETF -aixmd5 "$ulaz"`
    sadrzaj=`cat izlaz.tmp`
    if [[ "$hash1" == "$sadrzaj" ]];then
echo " 1 : "$ulaz" "
elif [[ "$hash2" == "$sadrzaj" ]];then
echo " 5 : "$ulaz" "
    elif [[ "$hash3" == "$sadrzaj" ]];then
echo " 6 : "$ulaz" "
elif [[ "$hash4" == "$sadrzaj" ]];then
echo " apr1 : "$ulaz" "
elif [[ "$hash5" == "$sadrzaj" ]];then
echo " aixmd5 : "$ulaz" "
fi

    done

apr1 ulaz38
