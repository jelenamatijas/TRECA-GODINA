#!/bin/bash

for f in *.crt; do
  if openssl dgst -sha224 -verify server_pub.pem -signature potpis.txt "$f" 2>/dev/null | grep -q "OK"; then
    echo "$f"
  fi
done
