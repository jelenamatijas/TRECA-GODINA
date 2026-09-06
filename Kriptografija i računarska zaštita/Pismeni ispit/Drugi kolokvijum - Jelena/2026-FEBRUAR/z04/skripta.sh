#!/bin/bash

otisci=otisak*
ulazi=ulaz*

for otisak in $otisci; do
    count=`cat "$otisak" | tr -dc '\$' | wc -m`
    hash=`cat "$otisak"`

    for ulaz in $ulazi; do
        sadrzaj=`cat "$ulaz"`
        
        if [[ "$count" -gt $1 ]]; then
            alg=`cat "$otisak" | cut -d'$' -f2`
            salt=`cat "$otisak" | cut -d'$' -f3`
            
            rez=`openssl passwd -"$alg" -salt "$salt" "$sadrzaj" 2>error.log`
        else
            alg="aixmd5"
            rez=`openssl passwd -"$alg" "$sadrzaj"`
        fi
        
        if [[ "$hash" == "$rez" ]]; then
            echo "$ulaz" : "$otisak"
        fi
    done
done
        

#ulaz18.txt : otisak12.txt
#ulaz22.txt : otisak13.txt
#ulaz30.txt : otisak18.txt
        
        
