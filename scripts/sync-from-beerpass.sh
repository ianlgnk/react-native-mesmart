#!/usr/bin/env bash
# Sincroniza este repositório público com o privado da Beerpass (Beerpass-Club/react-native-mesmart).
#
# Reaplica a limpeza da publicação: tira os SDKs proprietários dos fabricantes e troca a credencial da
# Paystore por um marcador. A limpeza é determinística, então o histórico já publicado sai idêntico e só
# os commits novos entram, por merge. Nada é mesclado se sobrar credencial, SDK ou achado do gitleaks.
#
# Uso (na raiz deste repositório, em main e sem alterações): scripts/sync-from-beerpass.sh [--push]
# Requisitos: git-filter-repo e gitleaks. Acesso à Beerpass pela sua chave SSH ou por BEERPASS_DEPLOY_KEY.
set -euo pipefail

UPSTREAM=${UPSTREAM:-git@github.com:Beerpass-Club/react-native-mesmart.git}
PUSH=false
[ "${1:-}" = "--push" ] && PUSH=true
ROOT=$(git rev-parse --show-toplevel)
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
fail() { echo "ERRO: $*" >&2; exit 1; }

[ "$(git -C "$ROOT" branch --show-current)" = main ] || fail "rode em main"
[ -z "$(git -C "$ROOT" status --porcelain)" ] || fail "há alterações não commitadas"

# Na Action, a chave de leitura vem de um segredo; localmente vale a sua chave SSH.
if [ -n "${BEERPASS_DEPLOY_KEY:-}" ]; then
  install -m 600 /dev/null "$WORK/key"
  printf '%s\n' "$BEERPASS_DEPLOY_KEY" > "$WORK/key"
  curl -fsSL https://api.github.com/meta | python3 -c \
    'import json,sys; [print("github.com", k) for k in json.load(sys.stdin)["ssh_keys"]]' > "$WORK/known_hosts"
  export GIT_SSH_COMMAND="ssh -i $WORK/key -o IdentitiesOnly=yes -o UserKnownHostsFile=$WORK/known_hosts"
fi

echo "› Baixando a Beerpass"
git clone --quiet --mirror "$UPSTREAM" "$WORK/upstream.git"
git clone --quiet --no-local "$WORK/upstream.git" "$WORK/clean"
git -C "$WORK/clean" fetch --quiet origin 'refs/tags/*:refs/tags/*'

echo "› Limpando o histórico"
cat > "$WORK/rules.txt" <<'RULES'
regex:SECRET_TOKEN: String = "[A-Za-z0-9]{32,}"==>SECRET_TOKEN: String = "REPLACE_WITH_PAYSTORE_SECRET_TOKEN"
regex:APPLICATION_ID: String = "[1-9][0-9]*"==>APPLICATION_ID: String = "0"
RULES
(cd "$WORK/clean" && git filter-repo --force --quiet --replace-text "$WORK/rules.txt" --invert-paths \
  --path-glob '*.aar' --path-glob '*.so' --path-glob '*/NeptuneLiteApi*.jar' --path-glob '*/neo-bridge.jar' \
  --prune-empty never)

echo "› Conferindo"
objects() { git -C "$WORK/clean" rev-list --objects --all; }
# Conta em vez de `grep -q`: com pipefail, um grep que para cedo derruba o git com SIGPIPE e a checagem
# passaria justamente quando há vazamento.
in_blobs() {
  local n
  n=$(objects | cut -d' ' -f1 | git -C "$WORK/clean" cat-file --batch | LC_ALL=C grep -a -c "$@" || true)
  [ "${n:-0}" -gt 0 ]
}
in_paths() {
  local n
  n=$(objects | awk '{print $2}' | grep -c -E "$1" || true)
  [ "${n:-0}" -gt 0 ]
}
# Nenhum valor já atribuído a um token ou segredo na Beerpass pode aparecer aqui, em nenhum arquivo.
git -C "$WORK/upstream.git" log -p --all --no-color \
  | grep -oE '[A-Za-z_]*(TOKEN|SECRET|Token|Secret)[A-Za-z_]*: String = "[^"]{16,}"' \
  | sed -E 's/.*"(.*)"/\1/' | grep -v -E '^0+$|^REPLACE_' | sort -u > "$WORK/values.txt" || true
if [ -s "$WORK/values.txt" ] && in_blobs -F -f "$WORK/values.txt"; then fail "sobrou o valor de um token da Beerpass"; fi
if in_blobs -E 'SECRET_TOKEN: String = "[A-Za-z0-9]{32,}"'; then fail "sobrou um token da Paystore"; fi
if in_blobs -E 'APPLICATION_ID: String = "[1-9]'; then fail "sobrou um APPLICATION_ID real"; fi
if in_paths '\.(aar|so)$|NeptuneLite|neo-bridge'; then fail "sobrou um SDK de fabricante"; fi
gitleaks git "$WORK/clean" --redact --no-banner --log-level warn --exit-code 1 || fail "o gitleaks achou um segredo"

echo "› Mesclando"
cd "$ROOT"
git fetch --quiet "$WORK/clean" main:refs/remotes/beerpass/main
# Tags já publicadas precisam sair idênticas; uma diferente interrompe aqui.
git fetch --quiet "$WORK/clean" 'refs/tags/*:refs/tags/*'
if git merge-base --is-ancestor beerpass/main HEAD; then
  echo "Já está sincronizado com a Beerpass."
else
  version=$(git show beerpass/main:package.json | python3 -c 'import json,sys; print(json.load(sys.stdin)["version"])')
  git merge --no-ff --quiet -m "chore: sincroniza com a Beerpass ($version)" beerpass/main
  git log --oneline -1
fi

if $PUSH; then
  git push --quiet origin main
  git push --quiet origin --tags
  echo "› Publicado"
fi
