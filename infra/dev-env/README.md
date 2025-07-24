# Ambiente de Desenvolvimento - Backend

Este pacote contém tudo o que você precisa para rodar o ambiente backend localmente via Docker utilizando imagens já publicadas no Docker Hub.

## Como utilizar

### Pré-requisitos

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- Internet para baixar as imagens

### Passos

1. **Execute o script de setup:**

```bash
./setup.sh
```
#### Este script:

- Baixa as imagens latest dos serviços (como gateway, iam-service, etc) do Docker Hub
- Sobe os containers com docker-compose

### Acesse os serviços

- Gateway: http://localhost:8080

- Os demais serviços estarão disponíveis nas portas configuradas dentro do docker-compose.yml

### Variáveis de Ambiente

Verifique o conteúdo do .env.example para saber quais variáveis são utilizadas.

⚠️ Renomeie .env.example para .env se desejar personalizar os valores.