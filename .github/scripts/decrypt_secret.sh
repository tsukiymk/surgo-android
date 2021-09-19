#!/bin/bash

decrypt() {
  gpg --quiet --batch --yes --decrypt --passphrase="$1" --output $3 $2
}

if [[ ! -z "$LARGE_SECRET_PASSPHRASE" ]]; then
  decrypt ${LARGE_SECRET_PASSPHRASE} ./release.gpg ./release.jks
else
  echo "LARGE_SECRET_PASSPHRASE is empty"
fi
