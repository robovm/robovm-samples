#!/bin/bash

IOS_SDK_VERSION=8.0
IOS_MIN_VERSION=6.0

if xcrun -f clang &> /dev/null; then
  CC=$(xcrun -f clang)
else
  CC=$(which clang)
fi

XCODE_PATH=$(xcode-select -p)
SIM_SYSROOT="$XCODE_PATH/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator$IOS_SDK_VERSION.sdk"
DEVICE_SYSROOT="$XCODE_PATH/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS$IOS_SDK_VERSION.sdk"

rm -rf build/device/ build/simulator/
mkdir -p build/device/
mkdir -p build/simulator/
for f in *.m; do
  echo "Building $f"
  CC -arch armv7 -miphoneos-version-min=$IOS_MIN_VERSION -fobjc-arc -c $f -o build/device/$(basename $f).o -isysroot "$DEVICE_SYSROOT"
  CC -arch i386 -miphoneos-version-min=$IOS_MIN_VERSION -fobjc-arc -c $f -o build/simulator/$(basename $f).o -isysroot "$SIM_SYSROOT"
done

ar -rs build/device/libiOSPlot.a build/device/*.o
ar -rs build/simulator/libiOSPlot.a build/simulator/*.o

lipo -create -arch armv7 build/device/libiOSPlot.a -arch i386 build/simulator/libiOSPlot.a -output build/libiOSPlot.a
lipo -info build/libiOSPlot.a
