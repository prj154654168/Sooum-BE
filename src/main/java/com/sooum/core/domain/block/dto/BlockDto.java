package com.sooum.core.domain.block.dto;

import jakarta.validation.constraints.NotNull;

public record BlockDto(
        @NotNull Long toMemberPk
) { }
