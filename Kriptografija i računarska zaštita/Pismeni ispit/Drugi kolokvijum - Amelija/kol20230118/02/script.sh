#!/bin/bash

echo "=== Sadržaj decrypted fajlova ==="
head decrypted1.txt decrypted10.txt decrypted11.txt 2>/dev/null

echo ""
echo "=== Sadržaj potpisa ==="
cat potpis.txt

echo ""
echo "=== Tip ključa ==="
file kljuc.key
head -2 kljuc.key

echo ""
echo "=== Verifikacija potpisa za sve decrypted fajlove ==="
for dec in decrypted*.txt; do
  if openssl dgst -sha1 -verify kljuc.key -signature potpis.txt -passin pass:sigurnost "$dec" 2>/dev/null; then
    echo "✅ Potpis se poklapa sa: $dec"
    
    # Uporedi sa ulaznim fajlovima
    for ulaz in ulaz*.txt; do
      if cmp -s "$dec" "$ulaz"; then
        echo "🎯 To odgovara ulaznom fajlu: $ulaz"
        echo ""
        echo "================================"
        echo "REŠENJE:"
        echo "Ulazna datoteka: $ulaz"
        echo "Sadržaj:"
        cat "$ulaz"
        echo "================================"
        exit 0
      fi
    done
  fi
done

echo ""
echo "=== Ako potpis ne radi, hajde da probamo kompletno dekriptovanje ==="
cipher="
