package com.sooum.global.config.jwt;

import lombok.Getter;

@Getter
public enum RedisTokenPathPrefix {
    ACCESS_TOKEN("blacklist:access:"),
    REFRESH_TOKEN("blacklist:refresh:");

    private final String prefix;

    RedisTokenPathPrefix(String prefix) {
        this.prefix = prefix;
    }
}
