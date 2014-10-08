# RoboVM ContractR sample

Sample app for iOS, Android and JavaFX which lets you add Clients, Tasks and keep track of the time you are working on these tasks and how much money your work is generating.

This sample gives an idea of how to share code between an iOS and Android app using native UI in both apps. 
The iOS and Android projects are using a shared core project which holds the Model part of the Model View Controller pattern. Please note that the code in these projects are in need of clean-up, so please let us know when you find strange things. 

Please also feel free to improve this sample and let us know.

## iOS app

The iOS app is built using native UI components and APIs available through the Cocoa bindings in RoboVM. Through the pom.xml this project depends on the core project which holds all domain objects and services for managing clients, tasks and the work performed.

## Android app

The Android app is built using Android studio and standrad Android components such as view XMLs, Fragments and ActionBar.

To run the Android sample, import the project into Android Studio.  

## JavaFX app

The UI is built using Scene builder and some basic styling has been made in the css for iOS. The JavaFX part is in desperate need for UI improvements so please feel free to contribute.
