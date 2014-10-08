[[ ! -d $WORKSPACE/$SDK_DIR ]] && mkdir -p $WORKSPACE/$SDK_DIR && (wget http://dl.google.com/android/android-sdk_r23-linux.tgz -O - | tar zx -C $WORKSPACE/$SDK_DIR --strip-components 1); echo

echo 'y' | $WORKSPACE/$SDK_DIR/tools/android --silent update sdk --no-ui --force --all --obsolete --filter platform-tools,build-tools-19.1.0,extra-android-m2repository,extra-android-support,extra-google-m2repository,android-15

echo $SIGNING_CERTIFICATE > certificate.crt
echo $SIGNING_KEY > server.key

openssl pkcs12 -export -in server.crt -inkey server.key -out keystore.p12 -name "rapidftr" -passin env:RAPIDFTR_KEY_PASSWORD -passout env:RAPIDFTR_KEY_PASSWORD

keytool -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 -srcstorepass env:RAPIDFTR_STORE_PASSWORD -destkeystore rapidftr.keystore -deststorepass env:RAPIDFTR_STORE_PASSWORD

cd RapidFTR-Android && mvn clean package -Psign -Dandroid.sdk.path=$WORKSPACE/$SDK_DIR -U
