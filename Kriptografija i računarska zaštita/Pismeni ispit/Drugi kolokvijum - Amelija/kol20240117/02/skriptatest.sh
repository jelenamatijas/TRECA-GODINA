#!/bin/bash

otisci=otisak*
ulazi=ulaz*

for ulaz in $ulazi; do
    lozinka=`cat "$ulaz"`

    for otisak in $otisci; do
        otisakcitav=`cat "$otisak"`

        raw_alg=`echo "$otisakcitav" | awk -F'$' '{print $2}'`
        if [[ "$raw_alg" == "apr1" ]]; then
            algoritam=1
        else
            algoritam="$raw_alg"
        fi

        salt=`echo "$otisakcitav" | awk -F'$' '{print $3}'`

        otisak1=`openssl passwd -"$algoritam" -salt "$salt" "$lozinka"`

        if [[ "$otisak1" == "$otisakcitav" ]]; then
            echo "$ulaz odgovara $otisak"
        fi
    done
done

