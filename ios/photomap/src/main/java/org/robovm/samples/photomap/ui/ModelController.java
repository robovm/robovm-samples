/*
 * Copyright (C) 2013-2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's PhotoMap sample (v1.1)
 * which is copyright (C) 2011-2014 Apple Inc.
 */
package org.robovm.samples.photomap.ui;

import java.util.List;

import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIPageViewController;
import org.robovm.apple.uikit.UIPageViewControllerDataSource;
import org.robovm.apple.uikit.UIStoryboard;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.photomap.PhotoAnnotation;

/**
 * A controller object that manages a simple model -- a collection of map
 * annotations
 * 
 * The controller serves as the data source for the page view controller; it
 * therefore implements pageViewController:viewControllerBeforeViewController:
 * and pageViewController:viewControllerAfterViewController:. It also implements
 * a custom method, viewControllerAtIndex: which is useful in the implementation
 * of the data source methods, and in the initial configuration of the
 * application.
 * 
 * There is no need to actually create view controllers for each page in advance
 * -- indeed doing so incurs unnecessary overhead. Given the data model, these
 * methods create, configure, and return a new view controller on demand.
 */
public class ModelController extends NSObject implements UIPageViewControllerDataSource {
    private List<PhotoAnnotation> pageData;
    private long currentPageIndex;

    public DataViewController getViewControllerAtIndex(int index, UIStoryboard storyboard) {
        // return the data view controller for the given index
        if (pageData == null || index >= pageData.size()) {
            return null;
        }

        // create a new view controller and pass suitable data
        DataViewController dataViewController = (DataViewController) storyboard
                .instantiateViewController("DataViewController");
        dataViewController.setDataObject(pageData.get(index));
        return dataViewController;
    }

    private int getIndexOfViewController(DataViewController viewController) {
        return pageData.indexOf(viewController.getDataObject());
    }

    @Override
    public UIViewController getViewControllerBefore(UIPageViewController pageViewController,
            UIViewController viewController) {
        PhotosViewController photosViewController = (PhotosViewController) pageViewController.getDelegate();

        if (!photosViewController.isPageAnimationFinished()) {
            // we are still animating don't return a previous view controller
            // too soon
            return null;
        }

        int index = getIndexOfViewController((DataViewController) viewController);
        if (index == 0 || index == -1) {
            // we are at the first page, don't go back any further
            return null;
        }

        index--;
        currentPageIndex = index;

        return getViewControllerAtIndex(index, viewController.getStoryboard());
    }

    @Override
    public UIViewController getViewControllerAfter(UIPageViewController pageViewController,
            UIViewController viewController) {
        PhotosViewController photosViewController = (PhotosViewController) pageViewController.getDelegate();

        if (!photosViewController.isPageAnimationFinished()) {
            // we are still animating don't return a next view controller too
            // soon
            return null;
        }

        int index = getIndexOfViewController((DataViewController) viewController);
        if (index == -1) {
            // we are at the last page, don't go back any further
            return null;
        }

        index++;
        currentPageIndex = index;

        if (index == pageData.size()) {
            return null;
        }
        return getViewControllerAtIndex(index, viewController.getStoryboard());
    }

    @Override
    public long getPresentationCount(UIPageViewController pageViewController) {
        return 0;
    }

    @Override
    public long getPresentationIndex(UIPageViewController pageViewController) {
        return 0;
    }

    public List<PhotoAnnotation> getPageData() {
        return pageData;
    }

    public void setPageData(List<PhotoAnnotation> pageData) {
        this.pageData = pageData;
    }

    public long getCurrentPageIndex() {
        return currentPageIndex;
    }
}
