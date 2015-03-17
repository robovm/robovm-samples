/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;

public class PeriodicElements extends NSObject {
    private static PeriodicElements instance = new PeriodicElements();

    private PeriodicElements () {
        setupElementsArray();
    }

    public static PeriodicElements sharedPeriodicElements () {
        return instance;
    }

    private Map<String, List<AtomicElement>> states;
    private Map<String, AtomicElement> elements;
    private Map<String, List<AtomicElement>> nameIndexes;
    private List<AtomicElement> elementsSortedByNumber;
    private List<AtomicElement> elementsSortedBySymbol;
    private List<String> elementNameIndexes;
    private String[] elementPhysicalStates;

    @SuppressWarnings("unchecked")
    private void setupElementsArray () {
        // create maps that contain the arrays of element data indexed by name
        elements = new HashMap<>();
        // physical state
        states = new HashMap<>();
        // unique first characters (for the Name index table)
        nameIndexes = new HashMap<>();

        // create empty array entries in the states map for each physical state
        states.put("Solid", new ArrayList<AtomicElement>());
        states.put("Liquid", new ArrayList<AtomicElement>());
        states.put("Gas", new ArrayList<AtomicElement>());
        states.put("Artificial", new ArrayList<AtomicElement>());

        // read the element data from the plist
        String path = NSBundle.getMainBundle().findResourcePath("Elements", "plist");
        NSArray<NSDictionary<NSString, NSObject>> elementsData = (NSArray<NSDictionary<NSString, NSObject>>)NSArray
            .read(new File(path));

        // iterate over the values in the raw elements dictionary
        for (NSDictionary<NSString, NSObject> data : elementsData) {
            // create an atomic element instance for each
            AtomicElement element = new AtomicElement(data);

            // store that item in the elements map with the name as the key
            elements.put(element.getName(), element);

            // add that element to the appropriate array in the physical state dictionary
            states.get(element.getState()).add(element);

            // get the element's initial letter
            String firstLetter = element.getName().substring(0, 1);

            List<AtomicElement> existingList = nameIndexes.get(firstLetter);
            // if an array already exists in the name index dictionary
            // simply add the element to it, otherwise create an array
            // and add it to the name index dictionary with the letter as the key

            if (existingList != null) {
                existingList.add(element);
            } else {
                List<AtomicElement> tempList = new ArrayList<>();
                nameIndexes.put(firstLetter, tempList);
                tempList.add(element);
            }
        }

        // create the dictionary containing the possible element states
        // and presort the states data
        elementPhysicalStates = new String[] {"Solid", "Liquid", "Gas", "Artificial"};
        presortElementsByPhysicalState();

        // presort the dictionaries now
        // this could be done the first time they are requested instead
        //
        presortElementInitialLetterIndexes();

        elementsSortedByNumber = presortElementsByNumber();
        elementsSortedBySymbol = presortElementsBySymbol();
    }

    /** @param state
     * @return the list of elements for the requested physical state */
    public List<AtomicElement> getElementsWithPhysicalState (String state) {
        return states.get(state);
    }

    /** @param letter
     * @return a list of elements for an initial letter (ie A, B, C, ...) */
    public List<AtomicElement> getElementsWithInitialLetter (String letter) {
        return nameIndexes.get(letter);
    }

    /** Presort each of the arrays for the physical states */
    private void presortElementsByPhysicalState () {
        for (String state : elementPhysicalStates) {
            presortElementsWithPhysicalState(state);
        }
    }

    private void presortElementsWithPhysicalState (String state) {
        sortElements(states.get(state), SortType.NAME);
    }

    /** Presort the name index lists so the elements are in the correct order */
    private void presortElementInitialLetterIndexes () {
        elementNameIndexes = new ArrayList<>(nameIndexes.keySet());
        Collections.sort(elementNameIndexes);

        for (String letter : elementNameIndexes) {
            presortElementNamesForInitialLetter(letter);
        }
    }

    private void presortElementNamesForInitialLetter (String letter) {
        sortElements(nameIndexes.get(letter), SortType.NAME);
    }

    private List<AtomicElement> presortElementsByNumber () {
        return sortElements(new ArrayList<>(elements.values()), SortType.ATOMIC_NUMBER);
    }

    private List<AtomicElement> presortElementsBySymbol () {
        return sortElements(new ArrayList<>(elements.values()), SortType.SYMBOL);
    }

    private List<AtomicElement> sortElements (List<AtomicElement> elements, SortType sortType) {
        Comparator<AtomicElement> comparator = null;

        switch (sortType) {
        case NAME:
            comparator = new Comparator<AtomicElement>() {
                @Override
                public int compare (AtomicElement lhs, AtomicElement rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            };
            break;
        case ATOMIC_NUMBER:
            comparator = new Comparator<AtomicElement>() {
                @Override
                public int compare (AtomicElement lhs, AtomicElement rhs) {
                    return lhs.getAtomicNumber() - rhs.getAtomicNumber();
                }
            };
            break;
        case SYMBOL:
            comparator = new Comparator<AtomicElement>() {
                @Override
                public int compare (AtomicElement lhs, AtomicElement rhs) {
                    return lhs.getSymbol().compareTo(rhs.getSymbol());
                }
            };
            break;
        }

        Collections.sort(elements, comparator);
        return elements;
    }

    public List<String> getElementNameIndexes () {
        return elementNameIndexes;
    }

    public List<AtomicElement> getElementsSortedByNumber () {
        return elementsSortedByNumber;
    }

    public List<AtomicElement> getElementsSortedBySymbol () {
        return elementsSortedBySymbol;
    }

    public String[] getElementPhysicalStates () {
        return elementPhysicalStates;
    }

    enum SortType {
        NAME, ATOMIC_NUMBER, SYMBOL
    }
}
