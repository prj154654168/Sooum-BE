package com.sooum.core.domain.card.fontsize;

public enum FontSize {
    BIG(300),
    SMALL(200);

    private final int size;

    public int getSize() {
        return size;
    }

    FontSize(int size) {
        this.size = size;
    }
}
