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
package org.robovm.samples.contractr.fx.ios;

import java.io.File;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;
import org.robovm.samples.contractr.fx.core.ContractRFXApp;

/**
 * 
 */
public class ContractRFXIOSApp  extends UIApplicationDelegateAdapter {

    private static final String IOS_PROPERTY_PREFIX = "ios.";
    
    public static class App extends ContractRFXApp {
        
        @Override
        public void init() throws Exception {
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
            super.clientModel = new ClientModel(clientManager);
            super.taskModel = new TaskModel(taskManager);
        }
        
        @Override
        public void start(Stage primaryStage) throws Exception {
            super.start(primaryStage);
            
            Scene scene = primaryStage.getScene();
            scene.getStylesheets().add(ContractRFXIOSApp.class.getResource("iOSTheme.css").toExternalForm());
        }
    }
    
    @Override
    public boolean didFinishLaunching(UIApplication application,
            UIApplicationLaunchOptions launchOptions) {
        
        Thread launchThread = new Thread() {
            @Override
            public void run() {
                Application.launch(App.class);
            }
        };
        launchThread.setDaemon(true);
        launchThread.start();

        return true;
    }
    
    public static void main(String[] args) throws Exception {
        
        InputStream is = null;
        Properties userProperties = new Properties();
        try {
            is = ContractRFXIOSApp.class.getResourceAsStream("/javafx.platform.properties");
            System.out.println(is);
            userProperties.load(is);
            String key = null;
            for (Entry<Object, Object> e : userProperties.entrySet()) {
                key = (String) e.getKey();
                System.setProperty(key.startsWith(IOS_PROPERTY_PREFIX)
                        ? key.substring(IOS_PROPERTY_PREFIX.length()) : key,
                        (String) e.getValue());
            }
            System.getProperties().list(System.out);

        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, ContractRFXIOSApp.class);
        }
    }
}
