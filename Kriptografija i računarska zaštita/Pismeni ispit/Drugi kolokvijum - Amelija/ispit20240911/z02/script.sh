#!/bin/bash

otisci=otisak*
ulazi=ulaz*

for otisak in $otisci; do
    otisakhash=`cat $otisak`
    count=`cat "$otisak" | tr -dc '\$' | wc -m`
    if [[ "$count" -gt 2 ]];then
    alg=`cat "$otisak" | cut -d'$' -f2`
    salt=`cat "$otisak" | cut -d'$' -f3`
    else
    alg="aixmd5"
     salt=`cat "$otisak" | cut -d'$' -f1`
    fi
 for ulaz in $ulazi; do
    sadrzaj=`cat "$ulaz"`
    hash=`openssl passwd -"$alg" -salt "$salt" "$sadrzaj"`

    if [[ "$hash" == "$otisakhash" ]]; then
echo "$ulaz : $otisak"
fi 
done

done
#ulaz14 otisak19
#ulaz8 otisak2
#ulaz4 otisak7
#ulaz15 otisak8
