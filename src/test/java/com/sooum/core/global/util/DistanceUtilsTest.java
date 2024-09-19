package com.sooum.core.global.util;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Optional;

@Slf4j
class DistanceUtilsTest {

    @Test
    void calculate() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(127.11, 37.53);
        Point point = geometryFactory.createPoint(coordinate);

        Double latitudeIsNull = DistanceUtils.calculate(point, Optional.empty(), Optional.of(124.12));
        Double longitudeIsNull = DistanceUtils.calculate(point, Optional.of(35.12), Optional.empty());
        Double latitudeAndLongitudeAreNull = DistanceUtils.calculate(point, Optional.empty(), Optional.empty());
        Double cardLocationIsNull = DistanceUtils.calculate(null, Optional.of(35.12), Optional.of(124.12));
        Double correctResult = DistanceUtils.calculate(point, Optional.of(37.55), Optional.of(127.12));
        log.info("correctResult : {}", correctResult);

        Assertions.assertThat(latitudeIsNull).isNull();
        Assertions.assertThat(longitudeIsNull).isNull();
        Assertions.assertThat(latitudeAndLongitudeAreNull).isNull();
        Assertions.assertThat(cardLocationIsNull).isNull();
        Assertions.assertThat(correctResult).isNotNull();
    }
}