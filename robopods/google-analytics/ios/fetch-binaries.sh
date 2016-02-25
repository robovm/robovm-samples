#/bin/sh

# This script fetches the Google Analytics SDK for development purposes
# You need to agree to Google's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=3.15
rm -f GoogleAnalyticsServicesiOS_$VERSION.zip
rm -rf analytics
mkdir analytics
curl -O https://dl.google.com/googleanalyticsservices/GoogleAnalyticsServicesiOS_$VERSION.zip
tar xzf GoogleAnalyticsServicesiOS_$VERSION.zip -C analytics
rm -rf libs/
mkdir libs
cp -r analytics/GoogleAnalyticsServicesiOS_$VERSION/libGoogleAnalyticsServices.a libs/
cp -r analytics/GoogleAnalyticsServicesiOS_$VERSION/libAdIdAccess.a libs/
rm GoogleAnalyticsServicesiOS_$VERSION.zip
rm -r analytics