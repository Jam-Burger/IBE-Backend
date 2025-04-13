package com.kdu.hufflepuff.ibe.model.enums;

import lombok.Getter;

@Getter
public enum ConfigType {
    GLOBAL,
    LANDING,
    ROOMS_LIST,
    CHECKOUT;

    public String getKey() {
        return "CONFIG#" + this.name();
    }
}