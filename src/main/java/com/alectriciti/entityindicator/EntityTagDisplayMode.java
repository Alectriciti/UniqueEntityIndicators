package com.alectriciti.entityindicator;

public enum EntityTagDisplayMode
{
    NONE(false, false, false),
    TEXT(true, false, false),
    TILE(false, true, false),
    OUTLINE(false, false, true),
    TEXT_TILE(true, true, false),
    TEXT_OUTLINE(true, false, true),
    TILE_OUTLINE(false, true, true),
    ALL(true, true, true);

    private final boolean text;
    private final boolean tile;
    private final boolean outline;

    EntityTagDisplayMode(boolean text, boolean tile, boolean outline)
    {
        this.text = text;
        this.tile = tile;
        this.outline = outline;
    }

    public boolean showsText() { return text; }
    public boolean showsTile() { return tile; }
    public boolean showsOutline() { return outline; }

    public static EntityTagDisplayMode from(boolean text, boolean tile, boolean outline)
    {
        for (EntityTagDisplayMode mode : values())
        {
            if (mode.text == text && mode.tile == tile && mode.outline == outline)
            {
                return mode;
            }
        }
        return NONE;
    }
}