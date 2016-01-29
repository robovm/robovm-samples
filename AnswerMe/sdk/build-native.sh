#!/bin/bash -x

IOS_SDK_VERSION=9.2
MIN_IOS_VERSION=8.0

SELF=$(basename $0)
BASE=$(cd $(dirname $0); pwd -P)

rm -rf $BASE/target/native
mkdir -p $BASE/target/native

CC=$(xcrun -f clang)
XCODE_PATH=$(xcode-select --print-path)
DEVICE_SDK=$XCODE_PATH/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS$IOS_SDK_VERSION.sdk
SIM_SDK=$XCODE_PATH/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator$IOS_SDK_VERSION.sdk

$CC -arch arm64  -miphoneos-version-min=$MIN_IOS_VERSION -isysroot $DEVICE_SDK -o $BASE/target/native/init-arm64.o  -c $BASE/src/main/native/init.m -fembed-bitcode
$CC -arch armv7  -miphoneos-version-min=$MIN_IOS_VERSION -isysroot $DEVICE_SDK -o $BASE/target/native/init-armv7.o  -c $BASE/src/main/native/init.m -fembed-bitcode
$CC -arch i386   -miphoneos-version-min=$MIN_IOS_VERSION -isysroot $SIM_SDK    -o $BASE/target/native/init-x86.o    -c $BASE/src/main/native/init.m
$CC -arch x86_64 -miphoneos-version-min=$MIN_IOS_VERSION -isysroot $SIM_SDK    -o $BASE/target/native/init-x86_64.o -c $BASE/src/main/native/init.m
lipo $BASE/target/native/init-*.o -create -output $BASE/target/native/init.o
