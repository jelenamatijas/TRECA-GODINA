#!/bin/bash

ulazi=ulaz*
otisak2=`cat otisak.txt | tr -d '\n'`

echo "Trazim datoteku sa otiskom..."

for ulaz in $ulazi; do
    otisak1=`openssl dgst -shake256 "$ulaz" | awk '{print $2}'`
    
    if [[ "$otisak1" == "$otisak2" ]]; then
        echo "Pronadjena datoteka: $ulaz"
        echo "Sadrzaj: `cat $ulaz`"
        echo ""
        
        # Konverzija imena datoteke u hex
        key_hex=`echo -n "$ulaz" | xxd -p | tr -d '\n'`
        echo "Ime datoteke u hex: $key_hex"
        echo "Duzina hex: ${#key_hex}"
        echo ""
        
        # Padding kljuca do odgovarajucih duzina
        # CAMELLIA-128 treba 32 hex znaka (16 bajtova)
        # CAMELLIA-192 treba 48 hex znakova (24 bajta)
        # CAMELLIA-256 treba 64 hex znaka (32 bajta)
        
        key128=`printf "%-32s" "$key_hex" | tr ' ' '0' | cut -c1-32`
        key192=`printf "%-48s" "$key_hex" | tr ' ' '0' | cut -c1-48`
        key256=`printf "%-64s" "$key_hex" | tr ' ' '0' | cut -c1-64`
        
        echo "Key 128-bit: $key128"
        echo "Key 192-bit: $key192"
        echo "Key 256-bit: $key256"
        echo ""
        
        # Testiranje ECB modova (ne trebaju IV)
        echo "Testiram: camellia-128-ecb"
        openssl enc -d -camellia-128-ecb -K "$key128" -in sifrat.dec -out temp.txt 2>/dev/null
        if [ -f temp.txt ] && cmp -s "$ulaz" temp.txt; then
            echo "RESENJE: ulaz55.txt, camellia-128-ecb, -K $key128"
            rm -f temp.txt
            exit 0
        fi
        
        echo "Testiram: camellia-192-ecb"
        openssl enc -d -camellia-192-ecb -K "$key192" -in sifrat.dec -out temp.txt 2>/dev/null
        if [ -f temp.txt ] && cmp -s "$ulaz" temp.txt; then
            echo "RESENJE: ulaz55.txt, camellia-192-ecb, -K $key192"
            rm -f temp.txt
            exit 0
        fi
        
        echo "Testiram: camellia-256-ecb"
        openssl enc -d -camellia-256-ecb -K "$key256" -in sifrat.dec -out temp.txt 2>/dev/null
        if [ -f temp.txt ] && cmp -s "$ulaz" temp.txt; then
            echo "RESENJE: ulaz55.txt, camellia-256-ecb, -K $key256"
            rm -f temp.txt
            exit 0
        fi
        
        # Testiranje CBC modova (trebaju IV)
        # Probamo sa IV od samih nula
        iv128="00000000000000000000000000000000"
        
        echo "Testiram: camellia-128-cbc"
        openssl enc -d -camellia-128-cbc -K "$key128" -iv "$iv128" -in sifrat.dec -out temp.txt 2>/dev/null
        if [ -f temp.txt ] && cmp -s "$ulaz" temp.txt; then
            echo "RESENJE: ulaz55.txt, camellia-128-cbc, -K $key128 -iv $iv128"
            rm -f temp.txt
            exit 0
        fi
        
        echo "Testiram: camellia-192-cbc"
        openssl enc -d -camellia-192-cbc -K "$key192" -iv "$iv128" -in sifrat.dec -out temp.txt 2>/dev/null
        if [ -f temp.txt ] && cmp -s "$ulaz" temp.txt; then
            echo "RESENJE: ulaz55.txt, camellia-192-cbc, -K $key192 -iv $iv128"
            rm -f temp.txt
            exit 0
        fi
        
        echo "Testiram: camellia-256-cbc"
        openssl enc -d -camellia-256-cbc -K "$key256" -iv "$iv128" -in sifrat.dec -out temp.txt 2>/dev/null
        if [ -f temp.txt ] && cmp -s "$ulaz" temp.txt; then
            echo "RESENJE: ulaz55.txt, camellia-256-cbc, -K $key256 -iv $iv128"
            rm -f temp.txt
            exit 0
        fi
        
        rm -f temp.txt
        echo ""
        echo "Nije pronadjeno resenje sa -K opcijom"
        break
    fi
done

echo "Zavrseno."
