#!/usr/bin/env bash
# 4단계 PoC: HoYoLAB daily-note 엔드포인트에 raw HTTP 요청을 보내본다.
# Spring 들어가기 전에 셸에서 200 받는 걸 먼저 확인하기 위함.
#
# 실행: backend/ 에서 ./scripts/poc-daily-note.sh
# 의존: macOS의 md5 (Linux면 md5sum으로 바꿔야 함)

set -euo pipefail

# ─── 1. .env.local 로딩 ───
# source 대신 grep으로 직접 파싱.
# 쿠키 값에 $o1 같은 패턴이 있으면 source가 셸 변수로 해석하려다 깨짐.
if [ ! -f .env.local ]; then
  echo "ERROR: .env.local 이 backend/ 에 없음" >&2
  exit 1
fi
read_env() {
  local key="$1"
  grep -E "^${key}=" .env.local | head -1 | sed -E "s/^${key}=//"
}
HOYO_COOKIE=$(read_env "HOYO_COOKIE")
GENSHIN_UID=$(read_env "GENSHIN_UID")
GENSHIN_SERVER=$(read_env "GENSHIN_SERVER")

: "${HOYO_COOKIE:?.env.local에 HOYO_COOKIE 누락}"
: "${GENSHIN_UID:?.env.local에 GENSHIN_UID 누락}"
: "${GENSHIN_SERVER:?.env.local에 GENSHIN_SERVER 누락}"

# ─── 2. salt (커뮤니티 추출값. 깨지면 갱신 필요) ───
SALT="6s25p5ox5y14umn1p61aqyyvbvvl3lrt"

# ─── 3. timestamp + 6자리 랜덤 문자열 ───
T=$(date +%s)
R=$(openssl rand -hex 3)   # 6자리 hex (0-9a-f) — DS 알고리즘은 alphanumeric이면 OK

# ─── 4. DS = "T,R,md5(salt=...&t=...&r=...)" ───
SIGN_INPUT="salt=${SALT}&t=${T}&r=${R}"
SIGN=$(printf '%s' "$SIGN_INPUT" | md5 -q)
DS="${T},${R},${SIGN}"

echo "[debug]"
echo "  T  = ${T}"
echo "  R  = ${R}"
echo "  DS = ${DS}"
echo

# ─── 5. 요청 ───
# 브라우저 캡처 결과: sg-public-api.hoyolab.com 의 /event/game_record/... 가 살아있는 경로
URL="https://sg-public-api.hoyolab.com/event/game_record/genshin/api/dailyNote?server=${GENSHIN_SERVER}&role_id=${GENSHIN_UID}"

# device_id/device_fp 헤더는 쿠키의 _HYVUUID/DEVICEFP와 일치해야 서버가 받아줌.
# 그래서 generate하지 않고 cookie에서 추출.
extract_cookie() {
  local name="$1"
  printf '%s' "$HOYO_COOKIE" | grep -oE "(^|; *)${name}=[^;]+" | head -1 | sed -E "s/^(; *)?${name}=//"
}
DEVICE_ID=$(extract_cookie "_HYVUUID")
DEVICE_FP=$(extract_cookie "DEVICEFP")
: "${DEVICE_ID:?쿠키에 _HYVUUID 가 없음 - HOYO_COOKIE 갱신 필요}"
: "${DEVICE_FP:?쿠키에 DEVICEFP 가 없음 - HOYO_COOKIE 갱신 필요}"

curl -sS -i "${URL}" \
  -H "Cookie: ${HOYO_COOKIE}" \
  -H "DS: ${DS}" \
  -H "Origin: https://act.hoyolab.com" \
  -H "Referer: https://act.hoyolab.com/" \
  -H "x-rpc-app_version: 1.5.0" \
  -H "x-rpc-client_type: 5" \
  -H "x-rpc-platform: 4" \
  -H "x-rpc-language: ko-kr" \
  -H "x-rpc-device_id: ${DEVICE_ID}" \
  -H "x-rpc-device_fp: ${DEVICE_FP}" \
  -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36"

echo