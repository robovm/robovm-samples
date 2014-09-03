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

## Run using Maven

To run in the iPhone simulator:
```
mvn robovm:iphone-sim
```
To run in the iPad simulator:
```
mvn robovm:ipad-sim
```
To run on a connected device:
```
mvn robovm:ios-device
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

## Short description of each sample


| Name              | Description | Demonstrates |
| ------------------| ----------- | ---------------------------------|
| [QuickContacts](QuickContacts/)     | Port of Apple's [QuickContacts](https://developer.apple.com/library/ios/samplecode/QuickContacts/Introduction/Intro.html) sample | How to use the Address Book UI controllers and various properties. Shows how to browse a list of Address Book contacts, display and edit a contact record, create a new contact record, and update a partial contact record. |
| [UICatalog](UICatalog/)         | Port of Apple's [UICatalog](https://developer.apple.com/library/ios/samplecode/UICatalog/Introduction/Intro.html) sample | How to create and customize user interface controls found in the UIKit framework, along with their various properties and styles. |
