import os
import re
import yaml

# --- Constantes e Configurações ---
ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__)))
DEV_ENV_DIR = os.path.join(ROOT_DIR, "infra", "dev-env")
DOCKER_REPOSITORY = os.environ.get("DOCKER_REPOSITORY", "carambolas314/play2gather-api")
VAR_PATTERN = re.compile(r'\$\{([a-zA-Z_][a-zA-Z0-9_]*)\}')

def dotenv_values(filepath):
    """Lê um arquivo .env e retorna um dicionário, tratando comentários."""
    values = {}
    if not os.path.isfile(filepath):
        return values
    with open(filepath, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#'):
                key, val = line.split('=', 1)
                values[key.strip()] = val.strip()
    return values

def detect_db_dependencies(compose_content):
    """Detecta dependências de banco de dados a partir do conteúdo do docker-compose."""
    detected = set()
    lower_content = compose_content.lower()
    if "mongo" in lower_content:
        detected.add("mongodb")
    if "postgres" in lower_content or "psql" in lower_content:
        detected.add("postgresql")
    if "redis" in lower_content:
        detected.add("redis")
    return list(detected)

def scan_services(root_dir):
    """Escaneia o diretório raiz em busca de microserviços válidos."""
    service_names = []
    for item in os.listdir(root_dir):
        service_path = os.path.join(root_dir, item)
        dockerfile_path = os.path.join(service_path, "Dockerfile")
        if os.path.isdir(service_path) and not item.startswith('.') and os.path.isfile(dockerfile_path):
            service_names.append(item)
    print(f"✅ Serviços detectados: {', '.join(service_names)}")
    return service_names

def process_service(service_name):
    """Processa um único serviço, coletando suas configurações e dependências."""
    print(f"⚙️  Processando serviço: {service_name}")
    service_path = os.path.join(ROOT_DIR, service_name)

    env_data = dotenv_values(os.path.join(service_path, ".env"))

    dependencies = []
    compose_path = os.path.join(service_path, "docker-compose.yml")
    if os.path.isfile(compose_path):
        with open(compose_path, 'r', encoding='utf-8') as f:
            dependencies = detect_db_dependencies(f.read())

    return {
        "name": service_name,
        "env": env_data,
        "dependencies": dependencies
    }

def generate_artifacts(services_data):
    """Gera o docker-compose.yml global e os arquivos .env."""

    compose_services = {}
    compose_volumes = {}
    central_env = {}
    example_env = {}

    for service in services_data:
        service_name = service["name"]
        env_data = service["env"]

        # --- Adiciona o serviço da aplicação ao compose ---
        app_service = {
            "image": f"{DOCKER_REPOSITORY}:{service_name}-latest",
            "container_name": service_name,
            "environment": [],
            "networks": ["play2gather-net"],
            "depends_on": []
        }

        # --- Configura as variáveis de ambiente para o container ---
        for key, val in env_data.items():
            prefixed_key = f"{service_name.upper()}_{key}"

            def replacer(match):
                var_name = match.group(1)
                return f"${{{service_name.upper()}_{var_name}}}"

            rewritten_val = VAR_PATTERN.sub(replacer, val)

            central_env[prefixed_key] = rewritten_val
            example_env[prefixed_key] = "<value>"

            # O container sempre usa a versão DOCKER da URL, se existir
            if key.endswith("_DOCKER"):
                # Passa a variável para o container com o nome genérico (sem _DOCKER)
                generic_key = key.replace("_DOCKER", "")
                app_service["environment"].append(f"{generic_key}=${{{prefixed_key}}}")
            elif not key.endswith("_LOCAL"):
                app_service["environment"].append(f"{key}=${{{prefixed_key}}}")

        if service_name == "gateway":
            app_service["ports"] = [f"${{{service_name.upper()}_PORT_EXTERNAL}}:8080"]

        # --- Adiciona os serviços de banco de dados ao compose ---
        for dep_type in service["dependencies"]:
            db_service_name = f"{service_name}-{dep_type}"
            app_service["depends_on"].append(db_service_name)

            db_port_key = f"{service_name.upper()}_DB_PORT_EXTERNAL"
            db_user_key = f"{service_name.upper()}_DB_USER"
            db_pass_key = f"{service_name.upper()}_DB_PASSWORD"
            db_name_key = f"{service_name.upper()}_DB_NAME"

            if dep_type == "postgresql":
                compose_services[db_service_name] = {
                    "image": "postgres:15",
                    "container_name": db_service_name,
                    "environment": [
                        f"POSTGRES_USER=${{{db_user_key}}}",
                        f"POSTGRES_PASSWORD=${{{db_pass_key}}}",
                        f"POSTGRES_DB=${{{db_name_key}}}"
                    ],
                    "ports": [f"${{{db_port_key}}}:5432"],
                    "volumes": [f"{db_service_name}-data:/var/lib/postgresql/data"],
                    "restart": "unless-stopped",
                    "networks": ["play2gather-net"]
                }
                compose_volumes[f"{db_service_name}-data"] = {}

            elif dep_type == "mongodb":
                compose_services[db_service_name] = {
                    "image": "mongo:7.0",
                    "container_name": db_service_name,
                    "environment": [
                        f"MONGO_INITDB_ROOT_USERNAME=${{{db_user_key}}}",
                        f"MONGO_INITDB_ROOT_PASSWORD=${{{db_pass_key}}}",
                        f"MONGO_INITDB_DATABASE=${{{db_name_key}}}"
                    ],
                    "ports": [f"${{{db_port_key}}}:27017"],
                    "volumes": [f"{db_service_name}-data:/data/db"],
                    "restart": "unless-stopped",
                    "networks": ["play2gather-net"]
                }
                compose_volumes[f"{db_service_name}-data"] = {}

        if not app_service["depends_on"]:
            del app_service["depends_on"]

        compose_services[service_name] = app_service

    # --- Monta o dicionário final do Compose ---
    final_compose_data = {
        "services": compose_services,
        "volumes": compose_volumes,
        "networks": {"play2gather-net": {"name": "play2gather-net", "driver": "bridge"}}
    }

    # --- Escreve os arquivos ---
    os.makedirs(DEV_ENV_DIR, exist_ok=True)

    print("\n📄 Gerando infra/dev-env/docker-compose.yml...")
    with open(os.path.join(DEV_ENV_DIR, "docker-compose.yml"), "w", encoding="utf-8") as f:
        yaml.dump(final_compose_data, f, sort_keys=False, default_flow_style=False, indent=2)

    print("📦 Gerando .env global na raiz...")
    with open(os.path.join(ROOT_DIR, ".env"), "w", encoding="utf-8") as f:
        for k, v in sorted(central_env.items()):
            f.write(f'{k}={v}\n')

    print("📦 Gerando infra/dev-env/.env.example...")
    with open(os.path.join(DEV_ENV_DIR, ".env.example"), "w", encoding="utf-8") as f:
        for k, v in sorted(example_env.items()):
            f.write(f"{k}={v}\n")

    print("🛠️  Gerando infra/dev-env/setup.sh...")
    with open(os.path.join(DEV_ENV_DIR, "setup.sh"), "w", encoding="utf-8") as f:
        f.write("#!/bin/bash\n\n")
        f.write('echo "🚀 Subindo containers com docker-compose..."\n')
        f.write("# Este script espera que um arquivo .env global seja criado a partir do .env na raiz.\n")
        f.write("docker-compose --env-file .env up\n")
    os.chmod(os.path.join(DEV_ENV_DIR, "setup.sh"), 0o755)

def main():
    """Função principal para orquestrar a geração do ambiente de desenvolvimento."""
    print("--- Iniciando Geração de Ambiente de Desenvolvimento ---")
    service_names = scan_services(ROOT_DIR)
    services_data = [process_service(name) for name in service_names]
    generate_artifacts(services_data)
    print("\n--- ✅ Processo Concluído com Sucesso! ---")

if __name__ == "__main__":
    main()