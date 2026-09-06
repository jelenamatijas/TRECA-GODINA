#!/bin/bash
otisci=otisak*
ulazi=ulaz*

for ulaz in $ulazi; do
    lozinka=`cat "$ulaz" | tr -d '\n'`
    for otisak in $otisci; do
        otisakcitav=`cat "$otisak" | tr -d '\n'`
        
        if [[ "$otisakcitav" == \$* ]]; then
            algoritam=`echo "$otisakcitav" | awk -F'$' '{print $2}'`
            salt=`echo "$otisakcitav" | awk -F'$' '{print $3}'`
        else
            algoritam="aixmd5"
            salt=`echo "$otisakcitav" | awk -F'$' '{print $1}'`
        fi
        
        otisak1=`openssl passwd -$algoritam -salt "$salt" "$lozinka"`
        
      
        
        if [[ "$otisak1" == "$otisakcitav" ]]; then
            echo "POKLAPANJE: $ulaz odgovara $otisak"
        fi
    done
done
