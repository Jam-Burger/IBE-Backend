package com.kdu.hufflepuff.ibe.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    LOGO("logos"),
    LANDING_BANNER("landing_banners"),
    ROOMS_PAGE_BANNER("rooms_page_banners"),
    ROOM("rooms");

    private final String path;
}
