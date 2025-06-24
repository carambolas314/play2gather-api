#!/bin/bash

echo "🚧 Atualizando dev-env com script Python..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_SCRIPT="$SCRIPT_DIR/generate_dev_env.py"

if ! command -v python &>/dev/null; then
  echo "❌ Python3 não encontrado. Instale antes de prosseguir."
  exit 1
fi

python "$PYTHON_SCRIPT"

if [ $? -eq 0 ]; then
  echo "✅ Ambiente dev-env atualizado com sucesso!"
else
  echo "❌ Ocorreu um erro ao atualizar o ambiente dev-env."
  exit 1
fi
