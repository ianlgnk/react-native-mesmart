#!/usr/bin/env bash
# Release completo: release-it (versao, changelog, tag, push, GitHub release) e em seguida npm publish.
# O npm publish roda separado porque, com autenticacao por navegador (security key), o release-it
# nao mostra o link de login e fica pedindo OTP.
#
# Uso: yarn release [flags do release-it]   (ex.: yarn release --dry-run nao publica nada)
set -euo pipefail
cd "$(dirname "$0")/.."

release-it --no-npm.publish "$@"

# dry-run, ajuda ou versao: nada a publicar
for arg in "$@"; do
  case "$arg" in
    --dry-run | -d | --help | -h | --version | -v) exit 0 ;;
  esac
done

# release cancelado no prompt do release-it: HEAD nao esta na tag da versao
VERSION="$(node -p "require('./package.json').version")"
if [ "$(git describe --exact-match --tags HEAD 2>/dev/null || true)" != "v$VERSION" ]; then
  echo "HEAD nao esta na tag v$VERSION (release cancelado?). Nao vou publicar." >&2
  exit 1
fi

echo "Publicando react-native-mesmart@$VERSION no npm (autentique pelo navegador se pedido)..."
npm publish
