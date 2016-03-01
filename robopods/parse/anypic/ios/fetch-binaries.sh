#/bin/sh

# This script fetches the Parse SDK for development purposes
# You need to agree to Parse's Terms of Services before
# being allowed to use the SDK
set -e
VERSION=1.12.0
rm -f parse-library-$VERSION.zip
rm -rf parse
mkdir parse
curl -O http://parse-ios.s3.amazonaws.com/00e620d6f8c3fd9f167fa0d297ed723d/parse-library-$VERSION.zip
tar xzf parse-library-$VERSION.zip -C parse
rm -rf libs/
mkdir libs
cp -r parse/Parse.framework libs/
cp -r parse/ParseFacebookUtilsV4.framework libs/
cp -r parse/ParseUI.framework libs/
cp -r parse/ParseTwitterUtils.framework libs/
rm parse-library-$VERSION.zip
rm -r parse