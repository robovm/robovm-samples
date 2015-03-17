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
 * Portions of this code is based on Apple Inc's Footprint sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.footprint.viewcontrollers;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.mapkit.MKMapPoint;

public class AAPLCoordinateConverter {
    public static class AAPLGeoAnchor {
        public CLLocationCoordinate2D latitudeLongitude;
        public CGPoint pixel;
    }

    public static class AAPLGeoAnchorPair {
        public AAPLGeoAnchor fromAnchor;
        public AAPLGeoAnchor toAnchor;
    }

    /** Class that contains a point in meters (east and south) with respect to an origin point (in geographic space) We use East &
     * South because when drawing on an image, origin (0,0) is on the top-left. So +eastMeters corresponds to +x and +southMeters
     * corresponds to +y */
    public static class AAPLEastSouthDistance {
        public double east;
        public double south;
    }

    // Floorplan pixels per meter
    private final double pixelsPerMeter;
    private final double radiansRotated;
    // We pick one of the anchors on the floorplan as an origin point that we will compute distance relative to.
    private final MKMapPoint fromAnchorMKPoint;
    private final CGPoint fromAnchorFloorplanPoint;

    public AAPLCoordinateConverter (CLLocationCoordinate2D topLeft, CLLocationCoordinate2D bottomRight, CGSize imageSize) {
        this(createAnchorPair(topLeft, bottomRight, imageSize));
    }

    public AAPLCoordinateConverter (AAPLGeoAnchorPair anchors) {
        // To compute the distance between two geographical co-ordinates, we first need to
        // convert to MapKit co-ordinates...
        fromAnchorFloorplanPoint = anchors.fromAnchor.pixel;
        fromAnchorMKPoint = MKMapPoint.create(anchors.fromAnchor.latitudeLongitude);
        MKMapPoint toAnchorMapkitPoint = MKMapPoint.create(anchors.toAnchor.latitudeLongitude);

        double xDistance = anchors.toAnchor.pixel.getX() - anchors.fromAnchor.pixel.getX();
        double yDistance = anchors.toAnchor.pixel.getY() - anchors.fromAnchor.pixel.getY();

        // ... so that we can use MapKit's helper function to compute distance.
        // this helper function takes into account the curvature of the earth.
        double distanceBetweenPointsMeters = MKMapPoint.getMetersBetween(fromAnchorMKPoint, toAnchorMapkitPoint);

        // Distance between two points in pixels (on the floorplan image)
        double distanceBetweenPointsPixels = Math.hypot(xDistance, yDistance);

        // Get the 2nd anchor's eastward/southward distance in meters from the first anchor point.
        AAPLEastSouthDistance hyp = convertPoint(fromAnchorMKPoint, toAnchorMapkitPoint);

        // This gives us pixels/meter
        pixelsPerMeter = distanceBetweenPointsPixels / distanceBetweenPointsMeters;

        // Angle of diagonal to east (in geographic)
        double angleFromEast = Math.atan2(hyp.south, hyp.east);

        // Angle of diagonal horizontal (in floorplan)
        double angleFromHorizontal = Math.atan2(yDistance, xDistance);

        // Rotation amount from the geographic anchor line segment
        // to the floorplan anchor line segment
        radiansRotated = angleFromHorizontal - angleFromEast;
    }

    private static AAPLGeoAnchorPair createAnchorPair (CLLocationCoordinate2D topLeft, CLLocationCoordinate2D bottomRight,
        CGSize imageSize) {
        AAPLGeoAnchor topLeftAnchor = new AAPLGeoAnchor();
        topLeftAnchor.latitudeLongitude = topLeft;
        topLeftAnchor.pixel = new CGPoint(0, 0);

        AAPLGeoAnchor bottomRightAnchor = new AAPLGeoAnchor();
        bottomRightAnchor.latitudeLongitude = bottomRight;
        bottomRightAnchor.pixel = new CGPoint(imageSize.getWidth(), imageSize.getHeight());

        AAPLGeoAnchorPair anchorPair = new AAPLGeoAnchorPair();
        anchorPair.fromAnchor = topLeftAnchor;
        anchorPair.toAnchor = bottomRightAnchor;

        return anchorPair;
    }

    /** Convenience function to convert a MapKit co-ordinate into a co-ordinate meters East/South relative to some origin.
     * @param fromAnchorMKPoint
     * @param toPoint
     * @return */
    public static AAPLEastSouthDistance convertPoint (MKMapPoint from, MKMapPoint to) {
        double metersPerMapPoint = MKMapPoint.getMetersPerMapPoint(from.toCoordinate().getLatitude());

        AAPLEastSouthDistance eastSouthDistance = new AAPLEastSouthDistance();
        eastSouthDistance.east = (to.getX() - from.getX()) * metersPerMapPoint;
        eastSouthDistance.south = (to.getY() - from.getY()) * metersPerMapPoint;

        return eastSouthDistance;
    }

    // Returns a CGPoint for where coordinates sit on the floorplan
    public CGPoint getPointFromCoordinate (CLLocationCoordinate2D coordinate) {
        // Get the distance east & south with respect to the first anchor point in meters
        AAPLEastSouthDistance toFix = convertPoint(fromAnchorMKPoint, MKMapPoint.create(coordinate));

        // Convert the east-south anchor point distance to pixels (still in east-south)
        CGPoint pixelsXYInEastSouth = new CGPoint(toFix.east, toFix.south).apply(CGAffineTransform.createScale(pixelsPerMeter,
            pixelsPerMeter));

        // Rotate the east-south distance to be relative to floorplan horizontal
        // This gives us an xy distance in pixels from the anchor point.
        CGPoint xy = pixelsXYInEastSouth.apply(CGAffineTransform.createRotation(radiansRotated));

        // however, we need the pixels from the (0, 0) of the floorplan
        // so we adjust by the position of the anchor point in the floorplan
        xy.setX(xy.getX() + fromAnchorFloorplanPoint.getX());
        xy.setY(xy.getY() + fromAnchorFloorplanPoint.getY());

        return xy;
    }

    public double getPixelsPerMeter () {
        return pixelsPerMeter;
    }
}
