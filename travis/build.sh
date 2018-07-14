#!/usr/bin/env bash
if [[ "${TRAVIS_BRANCH}" == "dev" ]]; then
  ./gradlew clean testDebug lintDebug assembleDebug --stacktrace
  
  # find compiled .apk file
  RELEASE=$(find ./app/build/outputs/apk/debug -type f -name "k4ever*.apk")
  # send it file via telegram bot
  curl \
  --silent \
  --form chat_id="${CHAT_ID}" \
  --form document=@"${RELEASE}" \
  "https://api.telegram.org/${TELEGRAM_TOKEN}/sendDocument" \
  >/dev/null 2>&1
  
else
  ./gradlew clean testDebug lintDebug --stacktrace
fi