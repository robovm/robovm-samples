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
package org.robovm.samples.photomap;

import java.io.File;
import java.util.List;

import org.robovm.apple.corelocation.CLGeocoder;
import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.mapkit.MKAnnotationAdapter;
import org.robovm.apple.uikit.UIImage;
import org.robovm.objc.block.VoidBlock2;

public class PhotoAnnotation extends MKAnnotationAdapter {
    private UIImage image;
    private final String imagePath;
    private final String title;
    private String subtitle;

    private PhotoAnnotation clusterAnnotation;
    private CLLocationCoordinate2D coordinate;
    private List<PhotoAnnotation> containedAnnotations;

    public PhotoAnnotation(String imagePath, String title, CLLocationCoordinate2D coordinate) {
        this.imagePath = imagePath;
        this.title = title;
        this.coordinate = coordinate;
    }

    public UIImage getImage() {
        if (image == null && imagePath != null) {
            image = new UIImage(new File(imagePath));
        }
        return image;
    }

    public String getStringForPlacemark(CLPlacemark placemark) {
        StringBuilder sb = new StringBuilder();
        if (placemark.getLocality() != null) {
            sb.append(placemark.getLocality());
        }

        if (placemark.getAdministrativeArea() != null) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(placemark.getAdministrativeArea());
        }

        if (sb.length() == 0 && placemark.getName() != null)
            sb.append(placemark.getName());

        return sb.toString();
    }

    public void updateSubtitleIfNeeded() {
        if (subtitle == null) {
            // for the subtitle, we reverse geocode the lat/long for a proper
            // location string name
            CLLocation location = new CLLocation(coordinate.getLatitude(), coordinate.getLongitude());
            CLGeocoder geocoder = new CLGeocoder();
            geocoder.reverseGeocodeLocation(location, new VoidBlock2<NSArray<CLPlacemark>, NSError>() {
                @Override
                public void invoke(NSArray<CLPlacemark> placemarks, NSError error) {
                    if (placemarks.size() > 0) {
                        CLPlacemark placemark = placemarks.get(0);
                        subtitle = String.format("Near %s", getStringForPlacemark(placemark));
                    }
                }
            });
        }
    }

    public List<PhotoAnnotation> getContainedAnnotations() {
        return containedAnnotations;
    }

    public void setContainedAnnotations(List<PhotoAnnotation> containedAnnotations) {
        this.containedAnnotations = containedAnnotations;
    }

    public PhotoAnnotation getClusterAnnotation() {
        return clusterAnnotation;
    }

    public void setClusterAnnotation(PhotoAnnotation clusterAnnotation) {
        this.clusterAnnotation = clusterAnnotation;
    }

    @Override
    public String getTitle() {
        if (containedAnnotations != null && containedAnnotations.size() > 0) {
            return String.format("%d Photos", containedAnnotations.size() + 1);
        }

        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public CLLocationCoordinate2D getCoordinate() {
        return coordinate;
    }

    @Override
    public void setCoordinate(CLLocationCoordinate2D newCoordinate) {
        this.coordinate = newCoordinate;
    }

    public String getImagePath() {
        return imagePath;
    }
}
