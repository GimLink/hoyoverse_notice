#!/usr/bin/env bash
# .env.local 의 비밀을 환경변수로 export 한 뒤 Spring Boot 앱을 띄운다.
#
# 사용: backend/ 에서 ./scripts/run-local.sh
#
# .env.local 을 그냥 source 하지 않는 이유:
# 호요버스 쿠키 안에 GA 쿠키 ($o1, $g0 등) 같이 $ 문자가 들어있어서
# 셸이 변수 확장을 시도하다 깨진다. 한 줄씩 grep 해서 추출.

set -euo pipefail

if [ ! -f .env.local ]; then
  echo "ERROR: backend/.env.local 이 없음" >&2
  exit 1
fi

read_env() {
  grep -E "^${1}=" .env.local | head -1 | sed -E "s/^${1}=//"
}

export HOYO_COOKIE=$(read_env "HOYO_COOKIE")
export GENSHIN_UID=$(read_env "GENSHIN_UID")
export GENSHIN_SERVER=$(read_env "GENSHIN_SERVER")

: "${HOYO_COOKIE:?.env.local에 HOYO_COOKIE 누락}"
: "${GENSHIN_UID:?.env.local에 GENSHIN_UID 누락}"
: "${GENSHIN_SERVER:?.env.local에 GENSHIN_SERVER 누락}"

exec ./mvnw spring-boot:run
