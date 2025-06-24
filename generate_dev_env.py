import os
import json
import yaml
from dotenv import dotenv_values

ROOT_DIR = os.path.abspath(os.path.dirname(__file__))
DEV_ENV_DIR = os.path.join(ROOT_DIR, "infra", "dev-env")
DOCKER_REPOSITORY = os.environ.get("DOCKER_REPOSITORY", "carambolas314/play2gather-api")

services_info = {}

print("🔄 Atualizando arquivos do ambiente dev-env...")

compose_data = {
    "version": "3.9",
    "services": {},
    "volumes": {}
}

central_env = {}
example_env = {}

def detect_db_dependencies(service_name, compose_content):
    detected = []
    lower = compose_content.lower()
    if "mongo" in lower:
        detected.append("mongodb")
    if "postgres" in lower or "psql" in lower:
        detected.append("postgresql")
    if "redis" in lower:
        detected.append("redis")
    return detected

for service_dir in os.listdir(ROOT_DIR):
    service_path = os.path.join(ROOT_DIR, service_dir)
    dockerfile_path = os.path.join(service_path, "Dockerfile")
    env_path = os.path.join(service_path, ".env")
    compose_path = os.path.join(service_path, "docker-compose.yml")

    if os.path.isdir(service_path) and os.path.isfile(dockerfile_path):
        print(f"🔍 Detectado serviço: {service_dir}")
        image_tag = f"{DOCKER_REPOSITORY}:{service_dir}-latest"

        service_compose = {
            "image": image_tag,
            "environment": []
        }

        if service_dir == "gateway":
            service_compose["ports"] = ["8080:8080"]

        if os.path.isfile(env_path):
            env_data = dotenv_values(env_path)
            for key, val in env_data.items():
                prefixed_key = f"{service_dir.upper()}_{key}"
                actual_key = key  # O nome da variável que o container espera
                service_compose["environment"].append(f"{actual_key}=${'{'+prefixed_key+'}'}")
                central_env[prefixed_key] = val
                example_env[prefixed_key] = "<value>"
            services_info[service_dir] = {"env": env_data, "dependencies": []}
        else:
            services_info[service_dir] = {"env": {}, "dependencies": []}

        compose_data["services"][service_dir] = service_compose

        if os.path.isfile(compose_path):
            with open(compose_path, encoding="utf-8") as f:
                try:
                    service_compose_data = f.read()
                    dependencies = detect_db_dependencies(service_dir, service_compose_data)
                    for dep in dependencies:
                        print(f"📦 {service_dir} depende de {dep.upper()}")
                        if dep == "mongodb":
                            mongo_service = f"{service_dir}-mongodb"
                            db_user_key = f"{service_dir.upper()}_DB_USER"
                            db_pass_key = f"{service_dir.upper()}_DB_PASSWORD"
                            db_name_key = f"{service_dir.upper()}_DB_NAME"
                            db_host_key = f"{service_dir.upper()}_DB_HOST"
                            uri_key = f"{service_dir.upper()}_MONGO_URI"
                            if uri_key in central_env:
                                uri = central_env[uri_key]
                                if "localhost" in uri and db_host_key in central_env:
                                    central_env[uri_key] = uri.replace("localhost", mongo_service)
                                    central_env[db_host_key] = mongo_service

                            compose_data["services"][mongo_service] = {
                                "image": "mongo:7.0",
                                "container_name": mongo_service,
                                "environment": {
                                    "MONGO_INITDB_ROOT_USERNAME": f"${{{service_dir.upper()}_DB_USER}}",
                                    "MONGO_INITDB_ROOT_PASSWORD": f"${{{service_dir.upper()}_DB_PASSWORD}}",
                                    "MONGO_INITDB_DATABASE": f"${{{service_dir.upper()}_DB_NAME}}"
                                },
                                "ports": [f"${{{service_dir.upper()}_DB_PORT}}:27017"],
                                "volumes": [f"{service_dir}-mongo-data:/data/db"],
                                "restart": "unless-stopped"
                            }
                            compose_data["volumes"][f"{service_dir}-mongo-data"] = {}
                            services_info[service_dir]["dependencies"].append("mongodb")
                        elif dep == "postgresql":
                            pg_service = f"{service_dir}-postgres"
                            compose_data["services"][pg_service] = {
                                "image": "postgres:16",
                                "container_name": pg_service,
                                "environment": {
                                    "POSTGRES_USER": f"${{{service_dir.upper()}_DB_USER}}",
                                    "POSTGRES_PASSWORD": f"${{{service_dir.upper()}_DB_PASSWORD}}",
                                    "POSTGRES_DB": f"${{{service_dir.upper()}_DB_NAME}}"
                                },
                                "ports": [f"${{{service_dir.upper()}_DB_PORT}}:5432"],
                                "volumes": [f"{service_dir}-pg-data:/var/lib/postgresql/data"],
                                "restart": "unless-stopped"
                            }
                            compose_data["volumes"][f"{service_dir}-pg-data"] = {}
                            services_info[service_dir]["dependencies"].append("postgresql")
                        elif dep == "redis":
                            redis_service = f"{service_dir}-redis"
                            compose_data["services"][redis_service] = {
                                "image": "redis:7.2",
                                "container_name": redis_service,
                                "ports": ["6379:6379"],
                                "restart": "unless-stopped"
                            }
                            services_info[service_dir]["dependencies"].append("redis")
                except Exception as e:
                    print(f"⚠️ Erro ao processar docker-compose de {service_dir}: {e}")

print("📄 Gerando docker-compose.yml...")
os.makedirs(DEV_ENV_DIR, exist_ok=True)
with open(os.path.join(DEV_ENV_DIR, "docker-compose.yml"), "w", encoding="utf-8") as f:
    yaml.dump(compose_data, f, sort_keys=False)

print("📦 Gerando .env.example...")
with open(os.path.join(DEV_ENV_DIR, ".env.example"), "w", encoding="utf-8") as f:
    for k, v in example_env.items():
        f.write(f"{k}={v}\n")

print("📦 Gerando .env.centralized dinâmico na raiz...")
with open(os.path.join(ROOT_DIR, ".env.centralized"), "w", encoding="utf-8") as f:
    for k, v in central_env.items():
        f.write(f"{k}={v}\n")

print("🛠️  Gerando setup.sh...")
with open(os.path.join(DEV_ENV_DIR, "setup.sh"), "w", encoding="utf-8") as f:
    f.write("""#!/bin/bash

echo "🚀 Subindo containers com docker-compose..."
docker-compose --env-file .env up
""")
os.chmod(os.path.join(DEV_ENV_DIR, "setup.sh"), 0o755)

print("✅ Ambiente dev-env atualizado com sucesso!")
print("📋 Serviços adicionados:")
for s in services_info:
    print(f" - {s}")
