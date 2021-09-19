#!/bin/bash

encrypt() {
  gpg --batch --yes --passphrase="$1" --cipher-algo AES256 --symmetric --output $3 $2
}

if [[ ! -z "$LARGE_SECRET_PASSPHRASE" ]]; then
  encrypt ${LARGE_SECRET_PASSPHRASE} ./release.jks ./release.gpg
else
  echo "LARGE_SECRET_PASSPHRASE is empty"
fi
