#!/bin/bash

echo "START - unlocking the keyguard for emulator"
$1/platform-tools/adb shell input keyevent 82
echo "END - unlocking the keyguard for emulator"
