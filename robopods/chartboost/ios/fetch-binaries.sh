#/bin/sh

# This script fetches the Chartboost SDK for development purposes
# You need to agree to Chartboost's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=6.1.1
rm -f Chartboost.tar.bz2
rm -rf chartboost
mkdir chartboost
curl -O https://s3.amazonaws.com/chartboost/sdk/$VERSION/Chartboost.tar.bz2
tar xzf Chartboost.tar.bz2 -C chartboost
rm -rf libs/
mkdir libs
cp -r chartboost/Chartboost.framework libs/
rm Chartboost.tar.bz2
rm -r chartboost