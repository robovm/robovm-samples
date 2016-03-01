# RoboVM sample projects

Each of the provided samples (unless otherwise noted in the sample's README.md) can be run using Gradle, Maven or Eclipse.

## Run using Gradle

First, build the project:
```
./gradlew build
```
To run in the iPhone simulator:
```
./gradlew launchIPhoneSimulator
```
To run in the iPad simulator:
```
./gradlew launchIPadSimulator
```
To run on a connected device:
```
./gradlew launchIOSDevice
```
To run a console app sample:
```
./gradlew launchConsole
```

## Compile & Run using Maven

To run in the iPhone simulator:
```
mvn compile robovm:iphone-sim
```
To run in the iPad simulator:
```
mvn compile robovm:ipad-sim
```
To run on a connected device:
```
mvn compile robovm:ios-device
```
To run a console app sample:
```
mvn compile robovm:console
```

## Run using Eclipse

In order to run these samples in Eclipse you may need to run the latest nightly version of the RoboVM for Eclipse plugin. Use the following update site in Eclipse to install it:

```
http://download.robovm.org/nightlies/eclipse/site.xml
```

Then you should be able to import the sample into Eclipse by selecting *File -> Import...* and then *Existing Projects into Workspace*.

To run in the iPhone simulator: Right-click the project. Choose *Run As -> iOS Simulator App (iPhone)*.

To run in the iPad simulator: Right-click the project. Choose *Run As -> iOS Simulator App (iPad)*.

To run on a connected device: Right-click the project. Choose *Run As -> iOS Device App*.

## Adding a new sample

1. Checkout the repo and switch to the 'snapshot' branch.
1. Create a new project in RoboVM Studio, saving it as a sub-directory of robovm-samples (or in one of the subcategory folders like ios or tvos).
1. During project creation, make sure all properties are correct (e.g. _package name_: org.robovm.samples.yoursample).
1. Replace the build.gradle in your project with a build.gradle from another sample. If you don't need any special build instructions remove the build.gradle file.
1. Replace the pom.xml in your project with a pom.xml from another sample.
1. Change the _artifactId_ and _name_ tags in the pom.xml file to match your own.
1. Add the sample in __both__ the root/settings.gradle and root/pom.xml (or subcategory pom.xml) files.
1. Insert a row about your sample into the table below (or the table of a subcategory readme).

## Available samples

| Name | Description | Demonstrates |
| ---- | ----------- | ------------ |
| [Console Samples](console/) | Console sample projects | |
| [iOS Samples](ios/) | iOS sample projects | |
| [iOS Samples (no-ib)](ios-no-ib/) | iOS sample projects without interface builder UI | |
| [RoboPods Samples](robopods/) | RoboPods sample projects | |
| [tvOS Samples](tvos/) | tvOS sample projects | |
| [AnswerMe](AnswerMe/) | Create Java SDK callable in Objective-C/Swift. | How to create an SDK out of Java code which can be easily used from Objective-C/Swift. |
| [ContractR](ContractR/) | Sample app for iOS, Android and JavaFX. | How to share code between an iOS and Android app using native UI in both apps. The iOS and Android projects are using a shared core project which holds the Model part of the Model View Controller pattern. Please note that the code in these projects are in need of clean-up, so please let us know when you find strange things. Please also feel free to improve this sample and let us know. |
| [CustomFrameworks](CustomFrameworks/) | Sample app using custom dynamic frameworks. | How to use custom Objective-C & Swift dynamic frameworks in a RoboVM Java project. |
| [MyJavaFramework](MyJavaFramework/) | Sample Xcode project calling Java classes via JNI. | How to package Java classes into an iOS dynamic framework and call the packaged classes via [JNI](http://docs.oracle.com/javase/8/docs/technotes/guides/jni/) from a native Objective-C app. |
