#!/bin/bash

# Caminho da pasta de infraestrutura
INFRA_DIR="./infra/dev-env"
DOCKER_REPOSITORY="${DOCKER_REPOSITORY:-carambolas314/play2gather-api}"

echo "🔄 Atualizando arquivos do ambiente dev-env..."

# Gera docker-compose.yml
echo "version: '3.8'" > $INFRA_DIR/docker-compose.yml
echo "services:" >> $INFRA_DIR/docker-compose.yml

for d in */ ; do
  if [ -f "${d}Dockerfile" ]; then
    SERVICE=$(basename "$d")
    echo "  $SERVICE:" >> $INFRA_DIR/docker-compose.yml
    echo "    image: \${DOCKER_REPOSITORY:-carambolas314/play2gather-api}:$SERVICE-latest" >> $INFRA_DIR/docker-compose.yml
    if [ "$SERVICE" == "gateway" ]; then
      echo "    ports:" >> $INFRA_DIR/docker-compose.yml
      echo "      - '8080:8080'" >> $INFRA_DIR/docker-compose.yml
    fi
    echo "    environment:" >> $INFRA_DIR/docker-compose.yml
    echo "      - PORT=8080" >> $INFRA_DIR/docker-compose.yml
    echo "" >> $INFRA_DIR/docker-compose.yml
  fi
done

# Gera .env.example genérico
cat <<EOF > $INFRA_DIR/.env.example
# Ambiente de desenvolvimento
PORT=8080
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=admin
DB_NAME=play2gather
JWT_SECRET=sua-chave-secreta
DOCKER_REPOSITORY=carambolas314/play2gather-api
EOF

# Gera setup.sh
cat <<'EOF' > $INFRA_DIR/setup.sh
#!/bin/bash

echo "🚀 Subindo containers com docker-compose..."
docker-compose up
EOF

chmod +x $INFRA_DIR/setup.sh

echo "✅ Arquivos atualizados com sucesso em $INFRA_DIR"
