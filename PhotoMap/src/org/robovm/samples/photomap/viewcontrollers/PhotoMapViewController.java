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
 * Portions of this code is based on Apple Inc's PhotoMap sample (v1.1)
 * which is copyright (C) 2011-2014 Apple Inc.
 */

package org.robovm.samples.photomap.viewcontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.robovm.apple.coregraphics.CGDataProvider;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.imageio.CGImageProperties;
import org.robovm.apple.imageio.CGImagePropertyGPS;
import org.robovm.apple.imageio.CGImagePropertyGPSData;
import org.robovm.apple.imageio.CGImageSource;
import org.robovm.apple.mapkit.MKAnnotation;
import org.robovm.apple.mapkit.MKAnnotationView;
import org.robovm.apple.mapkit.MKCoordinateRegion;
import org.robovm.apple.mapkit.MKCoordinateSpan;
import org.robovm.apple.mapkit.MKMapPoint;
import org.robovm.apple.mapkit.MKMapRect;
import org.robovm.apple.mapkit.MKMapSize;
import org.robovm.apple.mapkit.MKMapView;
import org.robovm.apple.mapkit.MKMapViewDelegateAdapter;
import org.robovm.apple.mapkit.MKPinAnnotationColor;
import org.robovm.apple.mapkit.MKPinAnnotationView;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBooleanBlock;
import org.robovm.samples.photomap.PhotoAnnotation;
import org.robovm.samples.photomap.views.LoadingStatus;

public class PhotoMapViewController extends UIViewController {
    private static final CLLocationCoordinate2D CherryLakeLocation = new CLLocationCoordinate2D(38.002493, -119.9078987);

    private List<MKAnnotation> photos;
    private final MKMapView allAnnotationsMapView;
    private final MKMapView mapView;

    private PhotosViewController photosViewController;

    public PhotoMapViewController () {
        setTitle("PhotoMap");
        getNavigationItem().setRightBarButtonItem(
            new UIBarButtonItem("Go", UIBarButtonItemStyle.Plain, new UIBarButtonItem.OnClickListener() {
                @Override
                public void onClick (UIBarButtonItem barButtonItem) {
                    zoomToCherryLake();
                }
            }));

        photosViewController = new PhotosViewController();

        allAnnotationsMapView = new MKMapView(CGRect.Zero());

        mapView = new MKMapView(getView().getBounds());

        mapView.setDelegate(new MKMapViewDelegateAdapter() {
            @Override
            public void didChangeRegion (MKMapView mapView, boolean animated) {
                updateVisibleAnnotations();
            }

            @Override
            public void didAddAnnotationViews (MKMapView mapView, NSArray<MKAnnotationView> views) {
                for (MKAnnotationView annotationView : views) {
                    if (!(annotationView.getAnnotation() instanceof PhotoAnnotation)) {
                        continue;
                    }

                    final PhotoAnnotation annotation = (PhotoAnnotation)annotationView.getAnnotation();

                    if (annotation.getClusterAnnotation() != null) {
                        // animate the annotation from it's old container's coordinate, to its actual coordinate
                        final CLLocationCoordinate2D actualCoordinate = annotation.getCoordinate();
                        CLLocationCoordinate2D containerCoordinate = annotation.getClusterAnnotation().getCoordinate();

                        // since it's displayed on the map, it is no longer contained by another annotation,
                        // (We couldn't reset this in -updateVisibleAnnotations because we needed the reference to it here
                        // to get the containerCoordinate)
                        annotation.setClusterAnnotation(null);

                        annotation.setCoordinate(containerCoordinate);

                        UIView.animate(0.3, new Runnable() {
                            @Override
                            public void run () {
                                annotation.setCoordinate(actualCoordinate);
                            }
                        });
                    }
                }
            }

            @Override
            public MKAnnotationView getAnnotationView (MKMapView mapView, MKAnnotation annotation) {
                final String annotationIdentifier = "Photo";

                if (mapView != PhotoMapViewController.this.mapView) {
                    return null;
                }

                if (annotation instanceof PhotoAnnotation) {
                    MKPinAnnotationView annotationView = (MKPinAnnotationView)mapView
                        .dequeueReusableAnnotationView(annotationIdentifier);

                    if (annotationView == null) {
                        annotationView = new MKPinAnnotationView(annotation, annotationIdentifier);
                        annotationView.setCanShowCallout(true);
                        annotationView.setPinColor(MKPinAnnotationColor.Red);

                        annotationView.setAnimatesDrop(true); // FIXME Without animatesDrop pin is not visible

                        UIButton disclosureButton = UIButton.create(UIButtonType.DetailDisclosure);
                        annotationView.setRightCalloutAccessoryView(disclosureButton);
                    } else {
                        annotationView.setAnnotation(annotation);
                    }
                    return annotationView;
                }
                return null;
            }

            /** User tapped the call out accessory 'i' button.
             * @param mapView
             * @param view
             * @param control */
            @Override
            public void calloutAccessoryControlTapped (MKMapView mapView, MKAnnotationView view, UIControl control) {
                PhotoAnnotation annotation = (PhotoAnnotation)view.getAnnotation();

                List<PhotoAnnotation> photosToShow = new ArrayList<>();
                photosToShow.add(annotation);
                photosToShow.addAll(annotation.getContainedAnnotations());

                photosViewController.setPhotosToShow(photosToShow);

                getNavigationController().pushViewController(photosViewController, true);
            }

            @Override
            public void didSelectAnnotationView (MKMapView mapView, MKAnnotationView view) {
                if (view.getAnnotation() instanceof PhotoAnnotation) {
                    PhotoAnnotation annotation = (PhotoAnnotation)view.getAnnotation();
                    annotation.updateSubtitleIfNeeded();
                }
            }
        });
        getView().addSubview(mapView);

        // center to Cherry Lake, but zoomed outward
        MKCoordinateRegion newRegion = new MKCoordinateRegion(CherryLakeLocation, new MKCoordinateSpan(5.0, 5.0));
        mapView.setRegion(newRegion);

        // now load all photos from Resources and add them as annotations to the mapview
        populateWorldWithAllPhotoAnnotations();
    }

    private List<MKAnnotation> loadPhotoSet (String path) {
        final List<MKAnnotation> photos = new ArrayList<>();
        // The bulk of our work here is going to be loading the files and looking up metadata
        // Thus, we see a major speed improvement by loading multiple photos simultaneously

        NSOperationQueue queue = new NSOperationQueue();
        queue.setMaxConcurrentOperationCount(8);

        List<String> photoPaths = NSBundle.getMainBundle().findResourcesPathsInSubPath("jpg", path);
        for (final String photoPath : photoPaths) {
            queue.addOperation(new Runnable() {
                @Override
                public void run () {
                    File file = new File(photoPath);
                    NSData imageData = NSData.read(file);
                    CGDataProvider dataProvider = CGDataProvider.create(imageData);
                    CGImageSource imageSource = CGImageSource.create(dataProvider, null);
                    CGImageProperties imageProperties = imageSource.getProperties(0, null);
                    // check if the image is geotagged
                    CGImagePropertyGPSData gpsInfo = imageProperties.getGPSData();
                    if (gpsInfo != null) {
                        CLLocationCoordinate2D coord = new CLLocationCoordinate2D();
                        coord.setLatitude(gpsInfo.getNumber(CGImagePropertyGPS.Latitude));
                        coord.setLongitude(gpsInfo.getNumber(CGImagePropertyGPS.Longitude));
                        if (gpsInfo.getString(CGImagePropertyGPS.LatitudeRef).equals("S")) {
                            coord.setLatitude(coord.getLatitude() * -1);
                        }
                        if (gpsInfo.getString(CGImagePropertyGPS.LongitudeRef).equals("W")) {
                            coord.setLongitude(coord.getLongitude() * -1);
                        }

                        String fileName = file.getName();
                        PhotoAnnotation photo = new PhotoAnnotation(photoPath, fileName, coord);

                        synchronized (photos) {
                            photos.add(photo);
                        }
                    }
                }
            });
        }

        queue.waitUntilAllOperationsAreFinished();

        return photos;
    }

    private void populateWorldWithAllPhotoAnnotations () {
        // add a temporary loading view
        final LoadingStatus loadingStatus = LoadingStatus.getDefaultLoadingStatus(getView().getFrame().getWidth());
        getView().addSubview(loadingStatus);

        // loading/processing photos might take a while -- do it asynchronously
        DispatchQueue.getGlobalQueue(DispatchQueue.PRIORITY_DEFAULT, 0).async(new Runnable() {
            @Override
            public void run () {
                System.out.println("Loading photos...");
                List<MKAnnotation> photos = loadPhotoSet("PhotoSet");
                if (photos == null) throw new UnsupportedOperationException("No photos found at path!");
                System.out.println("Photos loaded");

                PhotoMapViewController.this.photos = photos;

                DispatchQueue.getMainQueue().async(new Runnable() {
                    @Override
                    public void run () {
                        allAnnotationsMapView.addAnnotations(PhotoMapViewController.this.photos);
                        updateVisibleAnnotations();

                        loadingStatus.removeFromSuperviewWithFade();
                    }
                });
            }
        });
    }

    private PhotoAnnotation getAnnotationInGrid (MKMapRect gridMapRect, List<PhotoAnnotation> annotations) {
        // first, see if one of the annotations we were already showing is in this mapRect
        Set<? extends MKAnnotation> visibleAnnotationsInBucket = mapView.getAnnotations(gridMapRect);

        for (MKAnnotation annotation : annotations) {
            if (visibleAnnotationsInBucket.contains(annotation)) {
                return annotations.get(0);
            }
        }
        // otherwise, sort the annotations based on their distance from the center of the grid square,
        // then choose the one closest to the center to show
        final MKMapPoint centerMapPoint = new MKMapPoint(gridMapRect.getOrigin().getX() + gridMapRect.getSize().getWidth() / 2,
            gridMapRect.getOrigin().getY() + gridMapRect.getSize().getHeight() / 2);

        Comparator<MKAnnotation> comparator = new Comparator<MKAnnotation>() {
            @Override
            public int compare (MKAnnotation lhs, MKAnnotation rhs) {
                MKMapPoint mapPoint1 = MKMapPoint.create(lhs.getCoordinate());
                MKMapPoint mapPoint2 = MKMapPoint.create(rhs.getCoordinate());

                double distance1 = MKMapPoint.getMetersBetween(mapPoint1, centerMapPoint);
                double distance2 = MKMapPoint.getMetersBetween(mapPoint2, centerMapPoint);

                if (distance1 < distance2) {
                    return -1;
                }
                if (distance1 > distance2) {
                    return 1;
                }
                return 0;
            }
        };

        Collections.sort(annotations, comparator);
        return annotations.get(0);
    }

    private void updateVisibleAnnotations () {
        System.out.println("Updating visible annotations...");
        // This value to controls the number of off screen annotations are displayed.
        // A bigger number means more annotations, less chance of seeing annotation views pop in but decreased performance.
        // A smaller number means fewer annotations, more chance of seeing annotation views pop in but better performance.
        final float marginFactor = 2.0f;

        // Adjust this roughly based on the dimensions of your annotations views.
        // Bigger numbers more aggressively coalesce annotations (fewer annotations displayed but better performance).
        // Numbers too small result in overlapping annotations views and too many annotations on screen.
        final float bucketSize = 60.0f;

        // find all the annotations in the visible area + a wide margin to avoid popping annotation views in and out while
        // panning the map.
        MKMapRect visibleMapRect = mapView.getVisibleMapRect();
        MKMapRect adjustedVisibleMapRect = visibleMapRect.inset(-marginFactor * visibleMapRect.getSize().getWidth(),
            -marginFactor * visibleMapRect.getSize().getHeight());

        // determine how wide each bucket will be, as a MKMapRect square
        CLLocationCoordinate2D leftCoordinate = mapView.convertPointToCoordinateFromView(CGPoint.Zero(), getView());
        CLLocationCoordinate2D rightCoordinate = mapView.convertPointToCoordinateFromView(new CGPoint(bucketSize, 0), getView());

        double gridSize = MKMapPoint.create(rightCoordinate).getX() - MKMapPoint.create(leftCoordinate).getX();
        MKMapRect gridMapRect = new MKMapRect(); // 0, 0, gridSize, gridSize TODO
        gridMapRect.setSize(new MKMapSize(gridSize, gridSize));

        // condense annotations, with a padding of two squares, around the visibleMapRect
        double startX = Math.floor(adjustedVisibleMapRect.getOrigin().getX() / gridSize) * gridSize;
        double startY = Math.floor(adjustedVisibleMapRect.getOrigin().getY() / gridSize) * gridSize;
        double endX = Math.floor((adjustedVisibleMapRect.getOrigin().getX() + adjustedVisibleMapRect.getSize().getWidth())
            / gridSize)
            * gridSize;
        double endY = Math.floor((adjustedVisibleMapRect.getOrigin().getY() + adjustedVisibleMapRect.getSize().getHeight())
            / gridSize)
            * gridSize;

        // for each square in our grid, pick one annotation to show
        gridMapRect.getOrigin().setY(startY);

        while (gridMapRect.getOrigin().getY() <= endY) {
            gridMapRect.getOrigin().setX(startX);

            while (gridMapRect.getOrigin().getX() <= endX) {
                Set<? extends MKAnnotation> allAnnotationsInBucket = allAnnotationsMapView.getAnnotations(gridMapRect);
                Set<? extends MKAnnotation> visibleAnnotationsInBucket = mapView.getAnnotations(gridMapRect);

                if (allAnnotationsInBucket == null || visibleAnnotationsInBucket == null) continue;

                // we only care about PhotoAnnotations
                List<PhotoAnnotation> filteredAnnotationsInBucket = new ArrayList<>();
                for (MKAnnotation annotation : allAnnotationsInBucket) {
                    if (annotation instanceof PhotoAnnotation) {
                        filteredAnnotationsInBucket.add((PhotoAnnotation)annotation);
                    }
                }

                if (filteredAnnotationsInBucket.size() > 0) {
                    PhotoAnnotation annotationForGrid = getAnnotationInGrid(gridMapRect, filteredAnnotationsInBucket);

                    filteredAnnotationsInBucket.remove(annotationForGrid);

                    // give the annotationForGrid a reference to all the annotations it will represent
                    annotationForGrid.setContainedAnnotations(filteredAnnotationsInBucket);

                    mapView.addAnnotation(annotationForGrid);

                    for (final PhotoAnnotation annotation : filteredAnnotationsInBucket) {
                        // give all the other annotations a reference to the one which is representing them
                        annotation.setClusterAnnotation(annotationForGrid);
                        annotation.setContainedAnnotations(null);

                        // remove annotations which we've decided to cluster
                        if (visibleAnnotationsInBucket.contains(annotation)) {
                            final CLLocationCoordinate2D actualCoordinate = annotation.getCoordinate();
                            UIView.animate(0.3, new Runnable() {
                                @Override
                                public void run () {
                                    annotation.setCoordinate(annotation.getClusterAnnotation().getCoordinate());
                                }
                            }, new VoidBooleanBlock() {
                                @Override
                                public void invoke (boolean v) {
                                    annotation.setCoordinate(actualCoordinate);
                                    mapView.removeAnnotation(annotation);
                                }
                            });
                        }
                    }
                }

                gridMapRect.getOrigin().setX(gridMapRect.getOrigin().getX() + gridSize);
            }

            gridMapRect.getOrigin().setY(gridMapRect.getOrigin().getY() + gridSize);
        }
        System.out.println("Update completed");
    }

    private void zoomToCherryLake () {
        // clear any annotations in preparation for zooming
        mapView.removeAnnotations(mapView.getAnnotations());

        // center to Cherry Lake to see the rest of the annotations
        MKCoordinateRegion newRegion = new MKCoordinateRegion(CherryLakeLocation, new MKCoordinateSpan(0.05, 0.05));

        mapView.setRegion(newRegion, true);
    }
}
