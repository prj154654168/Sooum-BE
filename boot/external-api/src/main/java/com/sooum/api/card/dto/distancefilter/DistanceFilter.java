package com.sooum.api.card.dto.distancefilter;

public enum DistanceFilter {
    UNDER_1(0, 0.01),
    UNDER_5(0.01, 0.05),
    UNDER_10(0.05, 0.1),
    UNDER_20(0.1, 0.2),
    UNDER_50(0.2, 0.5);

    private final double minDistance;
    private final double maxDistance;

    DistanceFilter(double minDistance, double maxDistance) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }
}