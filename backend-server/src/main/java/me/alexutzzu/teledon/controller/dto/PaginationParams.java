package me.alexutzzu.teledon.controller.dto;

import jakarta.validation.constraints.Min;

public record PaginationParams(
        @Min(0) Integer page,
        @Min(1) Integer size
) {
    public PaginationParams {
        page = (page == null) ? 0 : page;
        size = (size == null) ? 10 : size;
    }
}
