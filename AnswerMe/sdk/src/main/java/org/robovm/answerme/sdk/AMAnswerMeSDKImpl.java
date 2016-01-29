/*
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.answerme.sdk;

import java.util.List;

import org.robovm.answerme.core.AnswerMeService;
import org.robovm.answerme.core.api.Topic;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.objc.ObjCClass;
import org.robovm.objc.ObjCObject;
import org.robovm.objc.annotation.Block;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.rt.VM;

/**
 * Actual implementation of the Objective-C {@code AMAnswerMeSDK} "abstract"
 * class. The Objective-C code in {@code init.m} will first initialize the VM
 * using standard JNI and then call {@link #initialize()} which registers this
 * class and all other Objective-C classes implemented in Java with the
 * Objective-C runtime.
 */
@CustomClass("AMAnswerMeSDKImpl")
public class AMAnswerMeSDKImpl extends NSObject {
    /**
     * The singleton {@link AMAnswerMeSDKImpl} instance.
     */
    private static AMAnswerMeSDKImpl instance;

    private final AnswerMeService answerMeService;

    /**
     * Registers all custom class (Objective-C classes defined in Java) in the
     * SDK packages with the Objective-C runtime.
     */
    @SuppressWarnings("unchecked")
    static void registerCustomClasses() {
        String packageName = AMAnswerMeSDKImpl.class.getName().substring(0, AMAnswerMeSDKImpl.class.getName().lastIndexOf('.'));
        for (Class<?> cls : VM.listClasses(NSObject.class, ClassLoader.getSystemClassLoader())) {
            if (cls.getName().startsWith(packageName)) {
                Foundation.log("Registering " + cls.getName() + " with the Objective-C runtime");
                ObjCClass.registerCustomClass((Class<? extends ObjCObject>) cls);
            }
        }
    }

    /**
     * Initializes the Java side of the SDK and registers the Objective-C
     * classes defined in Java with the Objective-C runtime. This is called by
     * the JNI initialization code in {@code init.m}.
     */
    static void initialize() {
        Foundation.log("Initializing " + AMAnswerMeSDKImpl.class.getName());
        instance = new AMAnswerMeSDKImpl();
        registerCustomClasses();
        Foundation.log("Initialization of " + AMAnswerMeSDKImpl.class.getName() + " done!");
    }

    /**
     * Returns the singleton {@link AMAnswerMeSDKImpl} instance. This is exposed
     * to Objective-C code using the {@code instance} selector.
     */
    @Method(selector = "instance")
    public static AMAnswerMeSDKImpl getInstance() {
        return instance;
    }

    private AMAnswerMeSDKImpl() {
        this.answerMeService = new AnswerMeService();
    }

    /**
     * Converts a {@link List} of Java {@link Topic} objects to an
     * {@link NSArray} of {@link AMTopicImpl} objects.
     */
    private NSArray<AMTopicImpl> toAMTopics(List<Topic> topics) {
        NSMutableArray<AMTopicImpl> result = new NSMutableArray<>(topics.size());
        for (Topic topic : topics) {
            result.add(new AMTopicImpl(topic));
        }
        return result;
    }

    /**
     * Asynchronously runs a query for topic summaries. Wraps
     * {@link AnswerMeService#search(String, org.robovm.answerme.core.Callback, org.robovm.answerme.core.Callback)}
     * and makes it callable from Objective-C using the
     * {@code searchWithQuery:onSuccess:onFailure:} selector.
     * 
     * @param query the query to run.
     * @param onSuccess Objective-C block which will be run when a result is
     *            returned successfully.
     * @param onFailure Objective-C block which will be run on failure.
     */
    @Method(selector = "searchWithQuery:onSuccess:onFailure:")
    public void search(String query, final @Block VoidBlock1<NSArray<AMTopicImpl>> onSuccess,
            final @Block VoidBlock1<NSString> onFailure) {

        answerMeService.search(
                query,
                l -> onSuccess.invoke(new NSArray<>(toAMTopics(l))),
                t -> onFailure.invoke(new NSString(t.getMessage())));
    }
}
