package com.alectriciti.entityindicator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(EntityTaggerConfig.GROUP)
public interface EntityTaggerConfig extends Config
{
    String GROUP = "entity_tagger";

    @ConfigItem(
            keyName = "showOverlays",
            name = "Show labels",
            description = "Draw labels over tagged players and NPCs"
    )
    default boolean showOverlays()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showNpcLabels",
            name = "Show NPC labels",
            description = "Draw labels over tagged NPCs"
    )
    default boolean showNpcLabels()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showPlayerLabels",
            name = "Show player labels",
            description = "Draw labels over tagged players"
    )
    default boolean showPlayerLabels()
    {
        return true;
    }

    @ConfigItem(
            keyName = "drawTileOutline",
            name = "Draw tile outline",
            description = "Outline the entity's tile behind the label"
    )
    default boolean drawTileOutline()
    {
        return true;
    }
}