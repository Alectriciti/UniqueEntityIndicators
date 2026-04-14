package com.alectriciti.entityindicator;

import java.awt.Color;

public class EntityTag
{
    private EntityTagScope scope;
    private String key;
    private String display_name;
    private String label;
    private int rgb;
    private boolean enabled = true;
    private EntityTagDisplayMode display_mode = EntityTagDisplayMode.ALL;

    public EntityTag()
    {
    }

    public EntityTag(EntityTagScope scope, String key, String display_name, String label, Color color)
    {
        this.scope = scope;
        this.key = key;
        this.display_name = display_name;
        this.label = label;
        this.rgb = color.getRGB();
    }

    public static EntityTag player(String player_name)
    {
        return new EntityTag(
                EntityTagScope.PLAYER,
                EntityTagStore.normalizePlayerName(player_name),
                player_name,
                player_name,
                new Color(0x70D6FF)
        );
    }

    public static EntityTag npc(int npc_id, String npc_name)
    {
        return new EntityTag(
                EntityTagScope.NPC,
                Integer.toString(npc_id),
                npc_name == null ? ("NPC " + npc_id) : npc_name,
                npc_name == null ? ("NPC " + npc_id) : npc_name,
                new Color(0xF9C74F)
        );
    }

    public EntityTagScope getScope()
    {
        return scope;
    }

    public void setScope(EntityTagScope scope)
    {
        this.scope = scope;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getDisplayName()
    {
        return display_name;
    }

    public void setDisplayName(String display_name)
    {
        this.display_name = display_name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getRgb()
    {
        return rgb;
    }

    public void setRgb(int rgb)
    {
        this.rgb = rgb;
    }

    public Color getColor()
    {
        return new Color(rgb, true);
    }

    public void setColor(Color color)
    {
        this.rgb = color.getRGB();
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public EntityTagDisplayMode getDisplayMode()
    {
        return display_mode == null ? EntityTagDisplayMode.ALL : display_mode;
    }

    public void setDisplayMode(EntityTagDisplayMode display_mode)
    {
        this.display_mode = display_mode;
    }

    public String getStorageKey()
    {
        return scope.name() + ":" + key;
    }

    public String serialize()
    {
        return getScope() + "|" + getKey() + "|" + getDisplayName() + "|" +
                getLabel() + "|" + getColor().getRGB() + "|" +
                getDisplayMode() + "|" + isEnabled();
    }

    public static EntityTag deserialize(String line)
    {
        try
        {
            String[] p = line.split("\\|");

            EntityTag tag = new EntityTag();
            tag.setScope(EntityTagScope.valueOf(p[0]));
            tag.setKey(p[1]);
            tag.setDisplayName(p[2]);
            tag.setLabel(p[3]);
            tag.setColor(new Color(Integer.parseInt(p[4])));
            tag.setDisplayMode(EntityTagDisplayMode.valueOf(p[5]));
            tag.setEnabled(Boolean.parseBoolean(p[6]));

            return tag;
        }
        catch (Exception e)
        {
            return null;
        }
    }

}