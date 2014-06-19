#!/usr/bin/env bash
set -xe

cd RapidFTR-Android
echo no | android create avd --force -n test -t $ANDROID_TARGET --abi $ANDROID_ABI
emulator -avd test -no-skin -no-audio -no-window -no-boot-anim &

cp -f travis/AndroidManifest.xml ./AndroidManifest.xml
mvn package -DskipTests
bundle install

./travis/wait_for_emulator.sh
adb shell input keyevent 82
calabash-android run `ls target/*.apk | head -1`
