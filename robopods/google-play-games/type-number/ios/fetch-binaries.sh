#/bin/sh

# This script fetches the Google Play Games Services SDK for development purposes
# You need to agree to Google's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=2.1
rm -f gpg-cpp-sdk.v$VERSION.zip
rm -rf gpgs
mkdir gpgs
curl -O https://developers.google.com/games/services/downloads/gpg-cpp-sdk.v$VERSION.zip
tar xzf gpg-cpp-sdk.v$VERSION.zip -C gpgs
rm -rf libs/
mkdir libs
cp -r gpgs/gpg-cpp-sdk/ios/gpg.framework libs/
cp -r gpgs/gpg-cpp-sdk/ios/gpg.bundle resources/
rm gpg-cpp-sdk.v$VERSION.zip
rm -r gpgs