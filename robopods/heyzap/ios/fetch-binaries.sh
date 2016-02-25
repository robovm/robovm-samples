#/bin/sh

# This script fetches the HeyZap SDK for development purposes
# You need to agree to HeyZap's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=9.3.5
rm -f heyzap-ads-sdk-$VERSION.zip
rm -rf heyzap-ads-sdk-$VERSION
curl -O https://hz-sdk.s3.amazonaws.com/sdk/heyzap-ads-sdk-$VERSION.zip
unzip heyzap-ads-sdk-$VERSION.zip
rm -rf libs/
mkdir libs
cp -r heyzap-ads-sdk-$VERSION/ios-sdk/HeyzapAds.framework libs/
rm heyzap-ads-sdk-$VERSION.zip
rm -r heyzap-ads-sdk-$VERSION