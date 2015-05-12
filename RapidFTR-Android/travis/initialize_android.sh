#!/usr/bin/env bash

set -e

INITIALIZATION_FILE="$ANDROID_HOME/.initialized-dependencies-$(git log -n 1 --format=%h -- $0)"

if [ ! -e ${INITIALIZATION_FILE} ]; then
  download-android
  echo y | android update sdk --no-ui --filter platform-tools > /dev/null
  echo y | android update sdk --no-ui --filter tools > /dev/null
  echo y | android update sdk --no-ui --filter build-tools-20.0.0 --all > /dev/null
  echo y | android update sdk --no-ui --filter android-15 > /dev/null
  echo y | android update sdk --no-ui --filter extra-google-m2repository --all > /dev/null
  echo y | android update sdk --no-ui --filter extra-android-m2repository --all > /dev/null
  echo y | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-15 --all > /dev/null

  touch ${INITIALIZATION_FILE}
fi
