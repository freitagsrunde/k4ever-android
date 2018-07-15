#!/usr/bin/env bash
if [[ "${TRAVIS_BRANCH}" == "dev" ]]; then
  ./gradlew clean testDebug lintDebug assembleDebug --stacktrace

  BASE_URL="https://api.telegram.org/${TELEGRAM_TOKEN}"
  
  # find compiled .apk file
  APK_FILE=$(find ./app/build/outputs/apk/debug -type f -name "k4ever*.apk")
  # send it file via telegram bot
  MESSAGE_ID=$(curl \
    --silent \
    --form chat_id="${CHAT_ID}" \
    --form document=@"${APK_FILE}" \
    "${BASE_URL}/sendDocument" \
    2>/dev/null \
    | jq 'if (.ok == true) then .result.message_id else empty end')
  
  # prepare telegram message to send as reply to the apk file
  if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
    PR_LINK_TEXT=$(cat <<EOF
[Pull Request](https://github.com/freitagsrunde/k4ever-android/pull/${TRAVIS_PULL_REQUEST})
EOF
)
  fi
  
  if [[ "${TRAVIS_TEST_RESULT}" == "0" ]]; then
    TRAVIS_TEST_RESULT="✅"
  else
    TRAVIS_TEST_RESULT="🔥"
  fi 
  
  COMMITS_INVOLVED=$(git log --oneline ${TRAVIS_COMMIT_RANGE})
  
  MESSAGE=$(cat <<EOF
*Travis Build [#${TRAVIS_BUILD_NUMBER}](https://travis-ci.org/freitagsrunde/k4ever-android/builds/${TRAVIS_BUILD_ID}) (${TRAVIS_EVENT_TYPE})*
Test Result: ${TRAVIS_TEST_RESULT}

Commits:
\`${COMMITS_INVOLVED}\`

${PR_LINK_TEXT}
EOF
)

  # send telegram chat message
  curl \
    --silent \
    -X POST \
    "${BASE_URL}/sendMessage" \
    -d "chat_id=${CHAT_ID}" \
    -d "text=${MESSAGE}" \
    -d "reply_to_message_id=${MESSAGE_ID}" \
    -d "parse_mode=markdown" \
    -d "disable_web_page_preview=true" \
    >/dev/null 2>&1

else
  ./gradlew clean testDebug lintDebug --stacktrace
fi

