package com.sooum.core.global.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public abstract class DistanceUtils {
    public static Double calculate (Point cardLocation, Double latitude, Double longitude) {
        if (isInValidLocationInfo(cardLocation, latitude, longitude)) {
            return null;
        }

        GeometryFactory geometry = new GeometryFactory();
        Coordinate coordinate = new Coordinate(latitude, longitude);
        Point targetPoint = geometry.createPoint(coordinate);

        return cardLocation.distance(targetPoint);
    }

    private static boolean isInValidLocationInfo(Point cardLocation, Double latitude, Double longitude) {
        return cardLocation == null || latitude == null || longitude == null;
    }
}
