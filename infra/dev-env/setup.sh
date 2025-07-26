#!/bin/bash

echo "🚀 Subindo containers com docker-compose..."
# Este script espera que um arquivo .env global seja criado a partir do .env na raiz.
docker-compose --env-file .env up
