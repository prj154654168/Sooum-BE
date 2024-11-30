package com.sooum.api.block.dto;

import jakarta.validation.constraints.NotNull;

public record BlockDto(
        @NotNull Long toMemberId
) { }
