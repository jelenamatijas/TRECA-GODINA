#!/bin/bash
ulazi=ulaz*
otisak1=`cat otisak.dec | tr -d '\n'`
sifrat="sifrat.dec"

# Pronadji fajl sa odgovarajucim SHA3-224 hash-em
for ulaz in $ulazi; do
    otisak2=`openssl dgst -sha3-224 "$ulaz" | awk '{print $2}'`
    if [[ "$otisak1" == "$otisak2" ]]; then
        echo "Fajl sa hash-em: $ulaz"
        target="$ulaz"
        break
    fi
done

# Lista AES algoritama za testiranje
algos=`openssl enc -list | grep -i aes`

# Dekriptuj sa svakim algoritmom
for algo in $algos; do
    algo_clean=`echo "$algo" | sed 's/^-//'`
    
    openssl enc -d -$algo_clean -in "$sifrat" -out "test.bin" -pass pass:"$algo_clean" 2>/dev/null
    
    size=`stat -c%s "test.bin" 2>/dev/null`
    
    # Ako je dekriptovanje uspelo (veci od 20 bajtova)
    if [ "$size" -gt 20 ]; then
        # Uporedi sa pronadjenim fajlom
        if cmp -s "test.bin" "$target"; then
            echo "Algoritam: $algo_clean"
            echo ""
            cat "$target"
            rm -f test.bin
            exit 0
        fi
    fi
done

rm -f test.bin

ulaz42.txt
aes-256-cfb1

