/*
 * Copyright (C) 2014 Trillian Mobile AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 * Portions of this code is based on Apple Inc's LazyTableImages sample (v1.5)
 * which is copyright (C) 2010-2014 Apple Inc.
 */

package org.robovm.samples.lazytableimages;

import java.lang.ref.WeakReference;

import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableData;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLConnection;
import org.robovm.apple.foundation.NSURLConnectionDataDelegateAdapter;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.foundation.NSURLResponse;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.samples.lazytableimages.operation.ParseOperation;
import org.robovm.samples.lazytableimages.viewcontrollers.RootViewController;

public class LazyTableImages extends UIApplicationDelegateAdapter {
    // the http URL used for fetching the top iOS paid apps on the App Store
    private static final String TOP_PAID_APPS_FEED = "http://phobos.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=75/xml";

    private UIWindow window;
    private UINavigationController navController;
    private RootViewController rootViewController;

    // the queue to run our "ParseOperation"
    private NSOperationQueue queue;
    // RSS feed network connection to the App Store
    private NSURLConnection appListFeedConnection;
    private NSMutableData appListData;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        rootViewController = new RootViewController();
        navController = new UINavigationController(rootViewController);

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(navController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);

        NSURLRequest urlRequest = new NSURLRequest(new NSURL(TOP_PAID_APPS_FEED));
        appListFeedConnection = new NSURLConnection(urlRequest, new NSURLConnectionDataDelegateAdapter() {
            // The following are delegate methods for NSURLConnection. Similar to callback functions, this is how
            // the connection object, which is working in the background, can asynchronously communicate back to
            // its delegate on the thread from which it was started - in this case, the main thread.

            /** Called when enough data has been read to construct an NSURLResponse object. */
            @Override
            public void didReceiveResponse (NSURLConnection connection, NSURLResponse response) {
                appListData = new NSMutableData();// start off with new data
            }

            /** Called with a single immutable NSData object to the delegate, representing the next portion of the data loaded from
             * the connection. */
            @Override
            public void didReceiveData (NSURLConnection connection, NSData data) {
                appListData.append(data); // append incoming data
            }

            /** Will be called at most once, if an error occurs during a resource load. No other callbacks will be made after. */
            @Override
            public void didFail (NSURLConnection connection, NSError error) {
                UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);

                handleError(error);

                appListFeedConnection = null; // release our connection
            }

            /** Called when all connection processing has completed successfully, before the delegate is released by the
             * connection. */
            @Override
            public void didFinishLoading (NSURLConnection connection) {
                appListFeedConnection = null; // release our connection

                UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);

                // create the queue to run our ParseOperation
                queue = new NSOperationQueue();

                // create an ParseOperation (NSOperation subclass) to parse the RSS feed data
                // so that the UI is not blocked
                ParseOperation parser = new ParseOperation(appListData);
                parser.setErrorHandler(new VoidBlock1<NSError>() {
                    @Override
                    public void invoke (final NSError parseError) {
                        DispatchQueue.getMainQueue().async(new Runnable() {
                            @Override
                            public void run () {
                                handleError(parseError);
                            }
                        });
                    }
                });

                // Referencing parser from within its completionBlock would create a retain cycle.
                final WeakReference<ParseOperation> weakParser = new WeakReference<ParseOperation>(parser);
                weakParser.get().setCompletionBlock(new Runnable() {
                    @Override
                    public void run () {
                        if (weakParser.get().getAppRecordList() != null) {
                            // The completion block may execute on any thread. Because operations
                            // involving the UI are about to be performed, make sure they execute
                            // on the main thread.
                            DispatchQueue.getMainQueue().async(new Runnable() {
                                @Override
                                public void run () {
                                    rootViewController.setEntries(weakParser.get().getAppRecordList());

                                    // tell our table view to reload its data, now that parsing has completed
                                    rootViewController.getTableView().reloadData();
                                }
                            });
                        }

                        // we are finished with the queue and our ParseOperation
                        queue = null;
                    }
                });

                queue.addOperation(parser); // this will start the "ParseOperation"

                // ownership of appListData has been transferred to the parse operation
                // and should no longer be referenced in this thread
                appListData = null;
            }
        });

        // Test the validity of the connection object. The most likely reason for the connection object
        // to be nulll is a malformed URL, which is a programmatic error easily detected during development
        // If the URL is more dynamic, then you should implement a more flexible validation technique, and
        // be able to both recover from errors and communicate problems to the user in an unobtrusive manner.
        if (appListFeedConnection == null) {
            throw new RuntimeException("Failure to create URL connection.");
        }

        // show in the status bar that network activity is starting
        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(true);

        return true;
    }

    /** Reports any error with an alert which was received from connection or loading failures.
     * @param error */
    private void handleError (NSError error) {
        String errorMessage = error.getLocalizedDescription();
        UIAlertView alertView = new UIAlertView("Cannot Show Top Paid Apps", errorMessage, null, "OK");
        alertView.show();
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, LazyTableImages.class);
        pool.close();
    }
}
