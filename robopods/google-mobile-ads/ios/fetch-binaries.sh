#/bin/sh

# This script fetches the Google Mobile Ads SDK for development purposes
# You need to agree to Google's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=7.7.0
rm -f googlemobileadssdkios-$VERSION.zip
rm -rf admob
mkdir admob
curl -O https://dl.google.com/googleadmobadssdk/googlemobileadssdkios-$VERSION.zip
unzip googlemobileadssdkios-$VERSION.zip -d admob/
rm -rf libs/
mkdir libs
cp -r admob/GoogleMobileAdsSdkiOS-$VERSION/GoogleMobileAds.framework libs/
rm googlemobileadssdkios-$VERSION.zip
rm -rf admob