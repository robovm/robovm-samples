#!/bin/bash

IOS_SDK_VERSION=8.1
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
mkdir -p build/device/armv7/
mkdir -p build/device/arm64/
mkdir -p build/simulator/i386/
mkdir -p build/simulator/x86_64/
for f in *.m; do
  echo "Building $f"
  CC -arch armv7 -miphoneos-version-min=$IOS_MIN_VERSION -fobjc-arc -c $f -o build/device/armv7/$(basename $f).o -isysroot "$DEVICE_SYSROOT"
  CC -arch arm64 -miphoneos-version-min=$IOS_MIN_VERSION -fobjc-arc -c $f -o build/device/arm64/$(basename $f).o -isysroot "$DEVICE_SYSROOT"
  CC -arch i386 -miphoneos-version-min=$IOS_MIN_VERSION -fobjc-arc -c $f -o build/simulator/i386/$(basename $f).o -isysroot "$SIM_SYSROOT"
  CC -arch x86_64 -miphoneos-version-min=$IOS_MIN_VERSION -fobjc-arc -c $f -o build/simulator/x86_64/$(basename $f).o -isysroot "$SIM_SYSROOT"
done

ar -rs build/device/armv7/libiOSPlot.a build/device/armv7/*.o
ar -rs build/device/arm64/libiOSPlot.a build/device/arm64/*.o
ar -rs build/simulator/i386/libiOSPlot.a build/simulator/i386/*.o
ar -rs build/simulator/x86_64/libiOSPlot.a build/simulator/x86_64/*.o

lipo -create \
    -arch armv7 build/device/armv7/libiOSPlot.a \
    -arch arm64 build/device/arm64/libiOSPlot.a \
    -arch i386 build/simulator/i386/libiOSPlot.a \
    -arch x86_64 build/simulator/x86_64/libiOSPlot.a \
    -output build/libiOSPlot.a
lipo -info build/libiOSPlot.a
