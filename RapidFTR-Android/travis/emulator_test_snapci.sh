#!/usr/bin/env bash

if (( ${SNAP_WORKER_TOTAL:-0} < 2 )); then
  echo "Not enough workers to run tests."
  exit -1
fi

case "$SNAP_WORKER_INDEX" in
  1) ./RapidFTR-Android/travis/emulator_test.sh features/login.feature features/navigation.feature features/register.feature ;;
  2) ./RapidFTR-Android/travis/emulator_test.sh features/search.feature features/sync.feature features/unverified_user.feature ;;
esac