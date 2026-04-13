package com.alectriciti.entityindicator;

public enum EntityTagDisplayMode
{
    TEXT,
    TILE,
    BOTH;

    public boolean showsText()
    {
        return this == TEXT || this == BOTH;
    }

    public boolean showsTile()
    {
        return this == TILE || this == BOTH;
    }
}