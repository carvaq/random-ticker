#!/bin/sh

mkdir "${ANDROID_HOME}/licenses" || true
echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > "${ANDROID_HOME}/licenses/android-sdk-license"
echo "84831b9409646a918e30573bab4c9c91346d8abd" > "${ANDROID_HOME}/licenses/android-sdk-preview-license"
"${ANDROID_HOME}/tools/bin/sdkmanager --update"
yes | "${ANDROID_HOME}/tools/bin/sdkmanager --licenses"

./gradlew assembleDebug testDebugUnitTest