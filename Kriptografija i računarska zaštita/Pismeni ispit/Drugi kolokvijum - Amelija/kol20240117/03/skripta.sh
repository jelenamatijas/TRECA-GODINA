#!/bin/bash

ulazi=ulaz*
sifrat="sifrat.dec"

: > error.log

for ulaz in $ulazi; do

  openssl enc -d -aes-128-cbc -in "$sifrat" -out d1.bin -pass pass:aes-128-cbc 2>>error.log
  openssl enc -d -aes-256-cbc -in "$sifrat" -out d2.bin -pass pass:aes-256-cbc 2>>error.log
  openssl enc -d -aes-128-ecb -in "$sifrat" -out d3.bin -pass pass:aes-128-ecb 2>>error.log
  openssl enc -d -aes-256-ecb -in "$sifrat" -out d4.bin -pass pass:aes-256-ecb 2>>error.log
  openssl enc -d -aes-192-cbc -in "$sifrat" -out d5.bin -pass pass:aes-192-cbc 2>>error.log
  openssl enc -d -aes-192-ecb -in "$sifrat" -out d6.bin -pass pass:aes-192-ecb 2>>error.log

  if cmp -s d1.bin "$ulaz"; then
      echo "aes-128-cbc : $ulaz"
  elif cmp -s d2.bin "$ulaz"; then
      echo "aes-256-cbc : $ulaz"
  elif cmp -s d3.bin "$ulaz"; then
      echo "aes-128-ecb : $ulaz"
  elif cmp -s d4.bin "$ulaz"; then
      echo "aes-256-ecb : $ulaz"
  elif cmp -s d5.bin "$ulaz"; then
      echo "aes-192-cbc : $ulaz"
  elif cmp -s d6.bin "$ulaz"; then
      echo "aes-192-ecb : $ulaz"
  fi

done

rm -f d*.bin

