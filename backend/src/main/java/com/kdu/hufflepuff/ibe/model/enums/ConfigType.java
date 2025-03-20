package com.kdu.hufflepuff.ibe.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigType {
    GLOBAL("global"),
    LANDING("landing");

    private final String path;

    public String getKey() {
        return "CONFIG#" + this.name();
    }
}