#!/bin/bash
# Add host alias to dev.rapidftr.com
# So that tests pointing to "dev.rapidftr.com" do not fail when the DNS is down
# Can be removed once the DNS is working properly again

set -e
export PATH=$PATH:$ANDROID_HOME/tools

echo "START - Adding host alias for dev.rapidftr.com"
adb remount
adb pull /system/etc/hosts .
echo "97.107.135.7  dev.rapidftr.com" >> hosts
adb push hosts /system/etc/
rm hosts
echo "STOP - Adding host alias for dev.rapidftr.com"
