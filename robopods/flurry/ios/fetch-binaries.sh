#/bin/sh

# This script fetches the Flurry SDK for development purposes
# You need to agree to Flurry's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=7.5.1
rm -f $VERSION.zip
rm -rf flurry
mkdir flurry
curl -L -O https://github.com/flurry/flurry-ios-sdk/archive/$VERSION.zip
tar xzf $VERSION.zip -C flurry
rm -rf libs/
mkdir libs
cp -r flurry/flurry-ios-sdk-$VERSION/Flurry/libFlurry_$VERSION.a libs/
cp -r flurry/flurry-ios-sdk-$VERSION/FlurryAds/libFlurryAds_$VERSION.a libs/
rm $VERSION.zip
rm -r flurry