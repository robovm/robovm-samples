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
| [BatteryStatus](BatteryStatus/)     | Port of Apple's [BatteryStatus](https://developer.apple.com/library/ios/samplecode/BatteryStatus/Introduction/Intro.html) sample | How to use the battery status properties and notifications provided via the iOS SDK. |
| [CurrentAddress](CurrentAddress/)     | Port of Apple's [CurrentAddress](https://developer.apple.com/library/ios/samplecode/CurrentAddress/Introduction/Intro.html) sample | How to use MapKit, displaying a map view and setting its region to current location. |
| [HelloWorld](HelloWorld/)     | Port of Apple's [HelloWorld](https://developer.apple.com/library/ios/samplecode/HelloWorld_iPhone/Introduction/Intro.html) sample | How to use a keyboard to enter text into a text field and how to display the text in a label. |
| [LaunchMe](LaunchMe/)     | Port of Apple's [LaunchMe](https://developer.apple.com/library/ios/samplecode/LaunchMe/Introduction/Intro.html) sample | How to implement a custom URL scheme to allow other applications to interact with your application. Shows how to handle an incoming URL request by overriding UIApplicationDelegate.openURL to properly parse and extract information from the requested URL before updating the user interface. |
| [LocateMe](LocateMe/)     | Port of Apple's [LocateMe](https://developer.apple.com/library/ios/samplecode/LocateMe/Introduction/Intro.html) sample | How to get the user's location and how to track changes to the user's location. |
| [MessageComposer](MessageComposer/)     | Port of Apple's [MessageComposer](https://developer.apple.com/library/ios/samplecode/MessageComposer/Introduction/Intro.html) sample | How to use the Message UI framework to compose and send email and SMS messages from within your application. |
| [MoviePlayer](MoviePlayer/)     | Port of Apple's [MoviePlayer](https://developer.apple.com/library/ios/samplecode/MoviePlayer_iPhone/Introduction/Intro.html) sample | How to use the Media Player framework to play a movie from a file or network stream, and configure the movie background color, playback controls, background color and image, scaling and repeat modes. It also shows how to draw custom overlay controls on top of the movie during playback. |
| [PhotoScroller](PhotoScroller/)     | Port of Apple's [PhotoScroller](https://developer.apple.com/library/ios/samplecode/PhotoScroller/Introduction/Intro.html) sample | How to use embedded UIScrollViews and CATiledLayer to create a rich user experience for displaying and paginating photos that can be individually panned and zoomed. CATiledLayer is used to increase the performance of paging, panning, and zooming with high-resolution images or large sets of photos. |
| [StreetScroller](StreetScroller/)     | Port of Apple's [StreetScroller](https://developer.apple.com/library/ios/samplecode/StreetScroller/Introduction/Intro.html) sample | How to subclass a UIScrollView and add infinite scrolling. |
| [Teslameter](Teslameter/)     | Port of Apple's [Teslameter](https://developer.apple.com/library/ios/samplecode/Teslameter/Introduction/Intro.html) sample | How to create a Teslameter, a magnetic field detector, with the use of the Core Location framework and display the raw x, y and z magnetometer values, a plotted history of those values, and a computed magnitude (size or strength) of the magnetic field. |
| [QuickContacts](QuickContacts/)     | Port of Apple's [QuickContacts](https://developer.apple.com/library/ios/samplecode/QuickContacts/Introduction/Intro.html) sample | How to use the Address Book UI controllers and various properties. Shows how to browse a list of Address Book contacts, display and edit a contact record, create a new contact record, and update a partial contact record. |
| [UICatalog](UICatalog/)         | Port of Apple's [UICatalog](https://developer.apple.com/library/ios/samplecode/UICatalog/Introduction/Intro.html) sample | How to create and customize user interface controls found in the UIKit framework, along with their various properties and styles. |
