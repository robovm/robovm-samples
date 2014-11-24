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

package org.robovm.samples.lazytableimages.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSOperation;
import org.robovm.apple.foundation.NSXMLParser;
import org.robovm.apple.foundation.NSXMLParserDelegateAdapter;
import org.robovm.objc.block.VoidBlock1;

public class ParseOperation extends NSOperation {
    // string contants found in the RSS feed
    private static final String IDStr = "id";
    private static final String NameStr = "im:name";
    private static final String ImageStr = "im:image";
    private static final String ArtistStr = "im:artist";
    private static final String EntryStr = "entry";

    // A block to call when an error is encountered during parsing.
    private VoidBlock1<NSError> errorHandler;

    private List<AppRecord> appRecordList;

    private NSData dataToParse;
    private final List<AppRecord> workingList = new ArrayList<>();
    private AppRecord workingEntry; // the current app record or XML entry being parsed
    private final StringBuilder workingPropertyString = new StringBuilder();
    private final List<String> elementsToParse;
    private boolean storingCharacterData;

    public ParseOperation (NSData data) {
        this.dataToParse = data;

        elementsToParse = Arrays.asList(IDStr, NameStr, ImageStr, ArtistStr);
    }

    /** Entry point for the operation. Given data to parse, use NSXMLParser and process all the top paid apps. */
    @Override
    public void main () {
        // The default implemetation of the -start method sets up an autorelease pool
        // just before invoking -main however it does NOT setup an exception handler
        // before invoking -main. If an exception is thrown here, the app will be
        // terminated.

        workingList.clear();

        // It's also possible to have NSXMLParser download the data, by passing it a URL, but this is not
        // desirable because it gives less control over the network, particularly in responding to
        // connection errors.
        NSXMLParser parser = new NSXMLParser(dataToParse);
        parser.setDelegate(new NSXMLParserDelegateAdapter() {
            @Override
            public void didStartElement (NSXMLParser parser, String elementName, String namespaceURI, String qName,
                Map<String, NSObject> attributeDict) {

                // entry: { id (link), im:name (app name), im:image (variable height) }
                if (elementName.equals(EntryStr)) {
                    workingEntry = new AppRecord();
                }
                storingCharacterData = elementsToParse.contains(elementName);
            }

            @Override
            public void didEndElement (NSXMLParser parser, String elementName, String namespaceURI, String qName) {
                if (workingEntry != null) {
                    if (storingCharacterData) {
                        String trimmedString = workingPropertyString.toString().trim().replaceAll("\\r|\\n", "");
                        workingPropertyString.setLength(0); // clear the string for next time

                        if (elementName.equals(IDStr)) {
                            workingEntry.appURLString = trimmedString;
                        } else if (elementName.equals(NameStr)) {
                            workingEntry.appName = trimmedString;
                        } else if (elementName.equals(ImageStr)) {
                            workingEntry.imageURLString = trimmedString;
                        } else if (elementName.equals(ArtistStr)) {
                            workingEntry.artist = trimmedString;
                        }
                    } else if (elementName.equals(EntryStr)) {
                        workingList.add(workingEntry);
                        workingEntry = null;
                    }
                }
            }

            @Override
            public void foundCharacters (NSXMLParser parser, String string) {
                if (storingCharacterData) {
                    workingPropertyString.append(string);
                }
            }

            @Override
            public void parseErrorOccurred (NSXMLParser parser, NSError parseError) {
                if (errorHandler != null) {
                    errorHandler.invoke(parseError);
                }
            }
        });
        parser.parse();

        if (!isCancelled()) {
            appRecordList = new ArrayList<>(workingList);
        }
        workingList.clear();
        dataToParse = null;
    }

    public List<AppRecord> getAppRecordList () {
        return appRecordList;
    }

    public void setErrorHandler (VoidBlock1<NSError> errorHandler) {
        this.errorHandler = errorHandler;
    }
}
