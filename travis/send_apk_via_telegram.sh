#!/usr/bin/env bash
if [[ "${TRAVIS_BRANCH}" == "dev" ]]; then
  RELEASE=$(find ./app/build/outputs/apk/debug -type f -name "k4ever*.apk")
  curl \
  --form chat_id="${CHAT_ID}" \
  --form document=@"${RELEASE}" \
  "https://api.telegram.org/${TELEGRAM_TOKEN}/sendDocument"
fi

