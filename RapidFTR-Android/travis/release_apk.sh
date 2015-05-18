#!/usr/bin/env bash

curl "https://raw.githubusercontent.com/andreafabrizi/Dropbox-Uploader/master/dropbox_uploader.sh" -o dropbox_uploader.sh
chmod +x dropbox_uploader.sh
echo $DROPBOX_CFG | sed 's/\\n/\'$'\n/g' > dropbox_config

if [ "$1" = "rapidreg" ]
    then
    (cd RapidFTR-Android && mvn clean package -DskipTests -Dbrand=rapidreg)
    ./dropbox_uploader.sh -f dropbox_config upload `ls RapidFTR-Android/target/rapidftr-*.apk | head -1` RapidREG/Android/RapidREG-dev.apk

    else
    (cd RapidFTR-Android && mvn clean package -DskipTests)
    ./dropbox_uploader.sh -f dropbox_config upload `ls RapidFTR-Android/target/rapidftr-*.apk | head -1` Releases/Android/RapidFTR-dev.apk

    fi