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

package org.robovm.samples.regions.ui;

import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.mapkit.MKAnnotation;
import org.robovm.apple.mapkit.MKCircle;
import org.robovm.apple.mapkit.MKMapView;
import org.robovm.apple.mapkit.MKOverlay;
import org.robovm.apple.mapkit.MKPinAnnotationColor;
import org.robovm.apple.mapkit.MKPinAnnotationView;
import org.robovm.samples.regions.RegionAnnotation;

public class RegionAnnotationView extends MKPinAnnotationView {
    private MKCircle radiusOverlay;
    private boolean isRadiusUpdated;
    private MKMapView map;
    private RegionAnnotation annotation;

    public RegionAnnotationView(RegionAnnotation annotation) {
        super(annotation, annotation.getTitle());

        setCanShowCallout(true);
        setMultipleTouchEnabled(false);
        setDraggable(true);
        setAnimatesDrop(true);
        this.annotation = annotation;
        setPinColor(MKPinAnnotationColor.Purple);

        if (annotation.getCoordinate() != null) {
            radiusOverlay = new MKCircle(annotation.getCoordinate(), annotation.getRadius());
        }
    }

    public void removeRadiusOverlay() {
        // Find the overlay for this annotation view and remove it if it has the
        // same coordinates.
        for (MKOverlay overlay : map.getOverlays()) {
            if (overlay instanceof MKCircle) {
                MKCircle circleOverlay = (MKCircle) overlay;
                CLLocationCoordinate2D coord = circleOverlay.getCoordinate();

                if (coord.getLatitude() == annotation.getCoordinate().getLatitude()
                        && coord.getLongitude() == annotation.getCoordinate().getLongitude()) {
                    map.removeOverlay(overlay);
                }
            }
        }

        isRadiusUpdated = false;
    }

    public void updateRadiusOverlay() {
        if (!isRadiusUpdated) {
            isRadiusUpdated = true;

            removeRadiusOverlay();

            setCanShowCallout(false);
            map.addOverlay(new MKCircle(annotation.getCoordinate(), annotation.getRadius()));
            setCanShowCallout(true);
        }
    }

    public void setMap(MKMapView map) {
        this.map = map;
        map.addOverlay(radiusOverlay);
    }

    @Override
    public void setAnnotation(MKAnnotation v) {
        super.setAnnotation(v);
        annotation = (RegionAnnotation) v;
    }
}
