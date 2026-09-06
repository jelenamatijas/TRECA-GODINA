#!/bin/bash
for cert in cert*; do
    echo
    echo ">>> $cert"

    openssl x509 -in "$cert" -noout -purpose | grep "SSL client" || echo "no client auth"

    openssl x509 -in "$cert" -noout -text | grep -qi "sha384" \
        && echo "sha384 OK" || echo "NO sha384"

    issuer=$(openssl x509 -in "$cert" -noout -issuer_hash)
    subject=$(openssl x509 -in "$cert" -noout -subject_hash)

    [[ "$issuer" == "$subject" ]] \
        && echo "self-signed" || echo "NOT self-signed"
done

