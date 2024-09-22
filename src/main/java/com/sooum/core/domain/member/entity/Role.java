package com.sooum.core.domain.member.entity;

import java.util.Arrays;

public enum Role {
    USER,
    BANNED;

    public static Role getRole(String s) {
        return Arrays.stream(values())
                .filter(role -> role.toString().equals(s))
                .findAny()
                .orElse(USER);
    }
}
