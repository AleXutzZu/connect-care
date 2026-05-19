#!/bin/sh

if [ ! -f /app/certs/private.pem ] || [ ! -f /app/certs/public.pem ]; then
  echo "Generating RSA key pair..."
  openssl genrsa -out /app/certs/private.pem 2048
  openssl rsa -in /app/certs/private.pem -pubout -out /app/certs/public.pem
else
  echo "RSA key pair already exists."
fi

exec "$@"