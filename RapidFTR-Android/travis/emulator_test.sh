#!/usr/bin/env bash
set -xe

cd RapidFTR-Android
echo no | android create avd --force -n test -t $ANDROID_TARGET --abi $ANDROID_ABI --skin WXGA800
emulator -avd test -no-skin -no-audio -no-window -no-boot-anim &

mvn clean package -DskipTests -P calabash
bundle install
bundle install -j4 --binstubs

./travis/wait_for_emulator.sh
adb shell input keyevent 82

if [ ! -n "$1" ]
    then
    echo $'\e[32m'"Running all features"$'\e[0m'
    bundle exec calabash-android run `ls target/*.apk | head -1` -f rerun --out rerun.txt -f pretty --tags ~@ignore

    else
    echo $'\e[32m'"Running only the following features:"$'\e[0m'
    for feature in "$@"
        do
        echo $'\e[32m'" [x] $feature"$'\e[0m'
        done
        echo ""
    bundle exec calabash-android run `ls target/*.apk | head -1` -f rerun --out rerun.txt -f pretty --tags ~@ignore $@
    fi