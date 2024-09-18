package com.sooum.core.domain.card.dto.distancefilter;

public enum DistanceFilter {
    UNDER_1(0, 1000),
    UNDER_5(1000, 5000),
    UNDER_10(5000, 10000),
    UNDER_20(10000, 20000),
    UNDER_50(20000, 50000);

    private final int minDistance;
    private final int maxDistance;

    DistanceFilter(int minDistance, int maxDistance) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
}