package com.sooum.core.global.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Optional;

public abstract class DistanceUtils {
    public static Double calculate(Point cardLocation, Optional<Double> latitude, Optional<Double> longitude) {
        if (isInValidLocationInfo(cardLocation, latitude, longitude)) {
            return null;
        }


        GeometryFactory geometry = new GeometryFactory();
        Coordinate coordinate = new Coordinate(latitude.get(), longitude.get());
        Point targetPoint = geometry.createPoint(coordinate);

        return cardLocation.distance(targetPoint);
    }

    private static boolean isInValidLocationInfo(Point cardLocation, Optional<Double> latitude, Optional<Double> longitude) {
        return cardLocation == null || latitude.isEmpty() || longitude.isEmpty();
    }
}
