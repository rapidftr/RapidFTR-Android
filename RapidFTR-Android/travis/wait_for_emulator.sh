#!/usr/bin/env bash

WAIT_TIMEOUT=60
WAIT_COUNTER=0

until [[ `adb devices | grep -E 'emulator-.*device'` ]]; do
  echo "Waiting for emulator to start..."
  let "WAIT_COUNTER += 1"
  if [[ $WAIT_COUNTER -gt $WAIT_TIMEOUT ]]; then
    echo "Emulator failed to start"
    exit 1
  fi
  sleep 1
done
echo "Emulator up and running"
