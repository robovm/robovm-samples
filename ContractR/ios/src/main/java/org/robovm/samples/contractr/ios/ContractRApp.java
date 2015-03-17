/*
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.contractr.ios;

import java.io.File;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIAppearance;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UINavigationBar;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UITabBar;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UITabBarControllerDelegateAdapter;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;

/**
 * App entry point.
 */
public class ContractRApp extends UIApplicationDelegateAdapter {

    public static final UIColor HIGHLIGHT_COLOR = 
            UIColor.fromRGBA(0x93 / 255.0, 0xc6 / 255.0, 0x23 / 255.0, 1.0);

    private UITabBarController tabBarController;
    private WorkViewController workViewController;
    private TasksViewController tasksViewController;
    private ClientsViewController clientsViewController;
    private ClientModel clientModel;
    private TaskModel taskModel;

    @Override
    public boolean didFinishLaunching(UIApplication application,
            UIApplicationLaunchOptions launchOptions) {

        /*
         * Prevent this Java UIApplicationDelegate instance and every Java
         * object reachable from it from being garbage collected until the
         * Objective-C UIApplication instance is deallocated.
         */
        application.addStrongRef(this);

        /*
         * Initialize the models. The SQLite database is kept in
         * <Application_Home>/Documents/db.sqlite. This directory is backed up
         * by iTunes. See http://goo.gl/BWlCGN for Apple's docs on the iOS file
         * system. 
         */
        try {
            Class.forName("SQLite.JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        File dbFile = new File(System.getenv("HOME"), "Documents/db.sqlite");
        dbFile.getParentFile().mkdirs();
        Foundation.log("Using db in file: " + dbFile.getAbsolutePath());
        SingletonConnectionPool connectionPool = new SingletonConnectionPool(
                "jdbc:sqlite:" + dbFile.getAbsolutePath());
        JdbcClientManager clientManager = new JdbcClientManager(connectionPool);
        JdbcTaskManager taskManager = new JdbcTaskManager(connectionPool);
        clientManager.setTaskManager(taskManager);
        taskManager.setClientManager(clientManager);
        clientModel = new ClientModel(clientManager);
        taskModel = new TaskModel(taskManager);

        /*
         * Create view controllers. We make them reachable from this
         * UIApplicationDelegate instance to prevent them from being GCed
         * prematurely.
         */
        workViewController = new WorkViewController(clientModel, taskModel);
        UIViewController reportsViewController = new ReportsViewController();
        clientsViewController = new ClientsViewController(clientModel,
                new AddClientViewController(clientModel), 
                new EditClientViewController(clientModel));
        tasksViewController = new TasksViewController(clientModel, taskModel,
                new AddTaskViewController(clientModel, taskModel), 
                new EditTaskViewController(clientModel, taskModel));

        UIFont ioniconsFont = UIFont.getFont("Ionicons", 30.0);
        UIImage workIconImage = createIconImage(ioniconsFont, '\uf1e1');
        UIImage reportsIconImage = createIconImage(ioniconsFont, '\uf2b5');
        UIImage clientsIconImage = createIconImage(ioniconsFont, '\uf1bf');
        UIImage tasksIconImage = createIconImage(ioniconsFont, '\uf16c');
        
        workViewController.setTabBarItem(new UITabBarItem("Work", workIconImage, 0));
        UINavigationController clientsNavigationController = new UINavigationController(
                clientsViewController);
        
        reportsViewController.setTabBarItem(new UITabBarItem("Reports", reportsIconImage, 0));
        
        clientsNavigationController.setTabBarItem(new UITabBarItem("Clients", clientsIconImage, 0));

        UINavigationController tasksNavigationController = new UINavigationController(
                tasksViewController);
        tasksNavigationController.setTabBarItem(new UITabBarItem("Tasks", tasksIconImage, 0));

        tabBarController = new UITabBarController();
        tabBarController.addChildViewController(workViewController);
        tabBarController.addChildViewController(reportsViewController);
        tabBarController.addChildViewController(clientsNavigationController);
        tabBarController.addChildViewController(tasksNavigationController);
        tabBarController.setSelectedIndex(0);
        tabBarController.setDelegate(new UITabBarControllerDelegateAdapter() {
            @Override
            public void didSelectViewController(UITabBarController tabBarController, UIViewController viewController) {
                if (viewController instanceof UINavigationController) {
                    ((UINavigationController) viewController).popToRootViewController(false);
                }
            }
            @Override
            public boolean shouldSelectViewController(UITabBarController tabBarController,
                    UIViewController viewController) {
                return viewController != tabBarController.getSelectedViewController();
            }
        });

        /* Customize the colors in the UI. */
        UITabBar appearanceTabBar = UIAppearance.getAppearance(UITabBar.class);
        appearanceTabBar.setTintColor(HIGHLIGHT_COLOR);
        UINavigationBar appearanceNavigationBar = UIAppearance.getAppearance(UINavigationBar.class);
        appearanceNavigationBar.setBarStyle(UIBarStyle.Black);
        appearanceNavigationBar.setBarTintColor(HIGHLIGHT_COLOR);
        appearanceNavigationBar.setTintColor(UIColor.white());
        UITableView appearanceTableView = UIAppearance.getAppearance(UITableView.class);
        appearanceTableView.setSeparatorColor(HIGHLIGHT_COLOR);
        UISwitch appearanceSwitch = UIAppearance.getAppearance(UISwitch.class);
        appearanceSwitch.setOnTintColor(HIGHLIGHT_COLOR);
        
        /* Create the UIWindow which is the root view in our UI. */
        UIWindow window = new UIWindow(UIScreen.getMainScreen().getBounds());
        window.makeKeyAndVisible();
        window.setRootViewController(tabBarController);
        /*
         * The Objective-C side won't retain our UIWindow. Prevent it from being
         * GCed and released by the Java side.
         */
        application.addStrongRef(window);

        return true;
    }

    private CGRect calculateIconDrawingRect(NSAttributedString s, CGSize imageSize) {
        CGSize iconSize = s.getSize();
        double xOffset = (imageSize.getWidth() - iconSize.getWidth()) / 2.0;
        double yOffset = (imageSize.getHeight() - iconSize.getHeight()) / 2.0;
        return new CGRect(xOffset, yOffset, iconSize.getWidth(), iconSize.getHeight());
    }
    
    private UIImage createIconImage(UIFont font, char code) {
        // Create a 30x30 image on iOS 6 and 60x60 on later iOS versions.
        double side = System.getProperty("os.version").startsWith("6.") ? 30 : 60;
        CGSize imageSize = new CGSize(side, side);
        UIGraphics.beginImageContext(imageSize, false, 0.0);
        NSAttributedStringAttributes attributes = new NSAttributedStringAttributes();
        attributes.setFont(font);
        NSAttributedString s = new NSAttributedString(Character.toString(code), attributes);
        s.draw(calculateIconDrawingRect(s, imageSize));
        UIImage image = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();
        return image;
    }
    
    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, ContractRApp.class);
        }
    }

}
