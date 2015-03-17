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
 * Portions of this code is based on Apple Inc's Regions sample (v1.1)
 * which is copyright (C) 2011 Apple Inc.
 */

package org.robovm.samples.regions;

import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.corelocation.CLRegion;
import org.robovm.apple.mapkit.MKAnnotationAdapter;

public class RegionAnnotation extends MKAnnotationAdapter {
    private CLRegion region;
    private CLLocationCoordinate2D coordinate;
    private double radius;
    private final String title;

    public RegionAnnotation (CLRegion region) {
        this.region = region;
        coordinate = region.getCenter();
        radius = region.getRadius();
        title = "Monitored Region";
    }

    public void setRadius (double radius) {
        this.radius = radius;
    }

    public double getRadius () {
        return radius;
    }

    @Override
    public String getTitle () {
        return title;
    }

    @Override
    public void setCoordinate (CLLocationCoordinate2D newCoordinate) {
        this.coordinate = newCoordinate;
    }

    @Override
    public CLLocationCoordinate2D getCoordinate () {
        return coordinate;
    }

    @Override
    public String getSubtitle () {
        return String.format("Lat: %.4f, Lon: %.4f, Rad: %.1fm", coordinate.getLatitude(), coordinate.getLongitude(), radius);
    }

    public CLRegion getRegion () {
        return region;
    }

    public void setRegion (CLRegion region) {
        this.region = region;
    }
}
