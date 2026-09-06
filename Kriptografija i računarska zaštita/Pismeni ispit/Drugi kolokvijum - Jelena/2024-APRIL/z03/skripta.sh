#!/bin/bash

keys=keystore*

for key in $keys; do
	keytool -importkeystore -srckeystore "$key" -destkeystore "p12$key" -deststoretype PKCS12 -srcstorepass sigurnost -deststorepass sigurnost
	openssl pkcs12 -in "p12$key" -out private.key -passin pass:sigurnost -passout pass:sigurnost -nocerts -nodes
    openssl rsautl -decrypt -in envelopa.dec -out decrypt.bin -inkey private.key -passin pass:sigurnost 2>>error.log
    cat decrypt.bin
done
	
#keystore20.jks
