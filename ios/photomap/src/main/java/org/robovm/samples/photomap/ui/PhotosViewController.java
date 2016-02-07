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

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIPageViewController;
import org.robovm.apple.uikit.UIPageViewControllerDelegate;
import org.robovm.apple.uikit.UIPageViewControllerNavigationDirection;
import org.robovm.apple.uikit.UIPageViewControllerNavigationOrientation;
import org.robovm.apple.uikit.UIPageViewControllerSpineLocation;
import org.robovm.apple.uikit.UIPageViewControllerTransitionStyle;
import org.robovm.apple.uikit.UIStoryboard;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.photomap.PhotoAnnotation;

@CustomClass("PhotosViewController")
public class PhotosViewController extends UIViewController implements UIPageViewControllerDelegate {
    private UIPageViewController pageViewController;
    private List<PhotoAnnotation> photosToShow;
    private boolean pageAnimationFinished;

    private ModelController modelController;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        modelController = new ModelController();

        // Do any additional setup after loading the view, typically from a nib.
        // Configure the page view controller and add it as a child view
        // controller.
        pageViewController = new UIPageViewController(UIPageViewControllerTransitionStyle.PageCurl,
                UIPageViewControllerNavigationOrientation.Horizontal, null);
        pageViewController.setDelegate(this);

        modelController.setPageData(photosToShow);

        UIStoryboard storyboard = new UIStoryboard("Main", null);
        DataViewController startingViewController = modelController.getViewControllerAtIndex(0, storyboard);

        NSArray<UIViewController> viewControllers = new NSArray<UIViewController>(startingViewController);
        pageViewController.setViewControllers(viewControllers,
                UIPageViewControllerNavigationDirection.Forward, false, null);

        updateNavBarTitle();

        pageViewController.setDataSource(modelController);

        addChildViewController(pageViewController);
        getView().addSubview(pageViewController.getView());
        pageViewController.didMoveToParentViewController(this);

        // add the page view controller's gesture recognizers to the book view
        // controller's view
        // so that the gestures are started more easily
        getView().setGestureRecognizers(pageViewController.getGestureRecognizers());

        pageAnimationFinished = true;
    }

    private void updateNavBarTitle() {
        if (modelController.getPageData().size() > 1) {
            setTitle(String.format("Photos (%d of %d)", modelController.getCurrentPageIndex() + 1, modelController
                    .getPageData()
                    .size()));
        } else {
            PhotoAnnotation viewController = modelController.getPageData().get(
                    (int) modelController.getCurrentPageIndex());
            setTitle(viewController.getTitle());
        }
    }

    public boolean isPageAnimationFinished() {
        return pageAnimationFinished;
    }

    @Override
    public void willTransition(UIPageViewController pageViewController, NSArray<UIViewController> pendingViewControllers) {
        pageAnimationFinished = false;
    }

    @Override
    public void didFinishAnimating(UIPageViewController pageViewController, boolean finished,
            NSArray<UIViewController> previousViewControllers, boolean completed) {

        // update the nav bar title showing which index we are displaying
        updateNavBarTitle();

        pageAnimationFinished = true;
    }

    @Override
    public UIPageViewControllerSpineLocation getSpineLocation(UIPageViewController pageViewController,
            UIInterfaceOrientation orientation) {
        // Set the spine position to "min" and the page view controller's view
        // controllers array to contain just one view
        // controller. Setting the spine position to
        // 'UIPageViewControllerSpineLocationMid' in landscape orientation sets
        // the doubleSided property to YES, so set it to NO here.
        UIViewController currentViewController = pageViewController.getViewControllers().get(0);

        NSArray<UIViewController> viewControllers = new NSArray<>(currentViewController);

        pageViewController.setViewControllers(viewControllers, UIPageViewControllerNavigationDirection.Forward, true,
                null);

        pageViewController.setDoubleSided(false);
        return UIPageViewControllerSpineLocation.Min;
    }

    @Override
    public UIInterfaceOrientationMask getSupportedInterfaceOrientations(UIPageViewController pageViewController) {
        return getSupportedInterfaceOrientations();
    }

    @Override
    public UIInterfaceOrientation getPreferredInterfaceOrientation(UIPageViewController pageViewController) {
        return getPreferredInterfaceOrientation();
    }

    public void setPhotosToShow(List<PhotoAnnotation> photosToShow) {
        this.photosToShow = photosToShow;
    }
}
