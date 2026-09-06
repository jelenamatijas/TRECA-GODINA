#!/bin/bash
ulazi=ulaz*

for ulaz in $ulazi; do
    sadrzaj=`cat "$ulaz" | openssl enc -d -base64 | tr -d ' \n\r'`
    count=`echo "$sadrzaj" | tr -dc '\$' |wc -m`
echo "$count"
    if [[ "$count" -gt 1 ]]; then
    alg=`echo "$sadrzaj" | cut -d'$' -f2`
    hash=`openssl passwd -salt ETF -"$alg" "$ulaz"`
    else 
     hash=`openssl passwd  -aixmd5 "$ulaz"`
    fi
    echo "$sadrzaj : $hash"
    if [[ "$sadrzaj" == "$hash" ]]; then
echo "$ulaz"
fi
done

#ulaz40
