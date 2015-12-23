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

#import <Foundation/Foundation.h>
#import "jni.h"

@class AMAnswerMeSDK;

/*
 * The singleton AMAnswerMeSDK instance. This will actually be an
 * AMAnswerMeSDKImpl instance. [AMAnswerMeSDK instance] defined below will
 * return this instance if it's been set by a previous call to the method.
 */
static AMAnswerMeSDK* _instance = NULL;

/*
 * Empty definitions and implementations of AMTopic and AMIcon. The real
 * implementations are in the AMTopicImpl and AMIconImpl classes in Java.
 * Think of the classes here as abstract base class even though Objective-C
 * doesn't have proper abstract classes.
 */
@interface AMTopic : NSObject @end
@implementation AMTopic @end
@interface AMIcon : NSObject @end
@implementation AMIcon @end

/*
 * "Abstract" base class for AMAnswerMeSDK. The implementation is in
 * AMAnswerMeSDKImpl defined in Java.
 */
@interface AMAnswerMeSDK : NSObject
+ (AMAnswerMeSDK*) instance;
@end
@implementation AMAnswerMeSDK

+ (AMAnswerMeSDK*) instance {

  if (_instance) {
    /*
     * We already have an instance created by a previous call to this method.
     * Return it.
     */
    return _instance;
  }

  JavaVMInitArgs vm_args;
  jint res;
  JavaVM *vm;
  JNIEnv *env;

  /*
   * Create the RoboVM "VM" using standard the JNI JNI_CreateJavaVM() function
   * which is exported by our framework.
   */
  vm_args.version = JNI_VERSION_1_2;
  vm_args.nOptions = 0;
  vm_args.options = NULL;
  res = JNI_CreateJavaVM(&vm, &env, &vm_args);
  if (res != JNI_OK) {
    [NSException raise:@"JNI_CreateJavaVM() failed" format:@"%d", res];
  }
  
  /*
   * Lookup the Java org.robovm.answerme.sdk.AMAnswerMeSDKImpl class and call
   * its initialize() method. Once that has been done successfully we're done
   * with JNI. Hooray!
   */
  jclass cls = (*env)->FindClass(env, "org/robovm/answerme/sdk/AMAnswerMeSDKImpl");
  if (!cls) {
    [NSException raise:@"Failed to find class org.robovm.answerme.sdk.AMAnswerMeSDKImpl" format:@""];
  }
  jmethodID mid = (*env)->GetStaticMethodID(env, cls, "initialize", "()V");
  if (!mid) {
    [NSException raise:@"Failed to find method org.robovm.answerme.sdk.AMAnswerMeSDKImpl.initialize()" format:@""];
  }
  (*env)->CallStaticVoidMethod(env, cls, mid);
  if ((*env)->ExceptionOccurred(env)) {
    [NSException raise:@"Call to method org.robovm.answerme.sdk.AMAnswerMeSDKImpl.initialize() failed" format:@""];
  }

  /*
   * AMAnswerMeSDKImpl.initialize() has now introduced the impl classes in the
   * Objective-C runtime.
   */
  Class answerMeSDKClass = NSClassFromString(@"AMAnswerMeSDKImpl");
  
  /*
   * Call [AMAnswerMeSDKImpl instance] to get the singleton AMAnswerMeSDKImpl
   * instance.
   */
  _instance = [answerMeSDKClass instance];
  return _instance;
}

@end
