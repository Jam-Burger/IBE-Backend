package com.kdu.hufflepuff.ibe.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    LOGO("logos"),
    BANNER("banners"),
    ROOM("rooms");

    private final String path;
}
