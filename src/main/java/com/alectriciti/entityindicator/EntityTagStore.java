package com.alectriciti.entityindicator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.config.ConfigManager;

@Singleton
public class EntityTagStore
{
    private static final String GROUP = "entity_tagger";
    private static final String KEY = "tags";

    private static final Type LIST_TYPE = new TypeToken<List<EntityTag>>() {}.getType();

    private final ConfigManager config_manager;
    private final Gson gson;

    private final Map<String, EntityTag> tags = new LinkedHashMap<>();

    @Inject
    public EntityTagStore(ConfigManager config_manager, Gson gson)
    {
        this.config_manager = config_manager;
        this.gson = gson;
    }

    public void load()
    {
        tags.clear();

        String serialized = config_manager.getConfiguration(GROUP, KEY);
        if (serialized == null || serialized.isBlank())
        {
            return;
        }

        List<EntityTag> loaded = gson.fromJson(serialized, LIST_TYPE);
        if (loaded == null)
        {
            return;
        }

        for (EntityTag tag : loaded)
        {
            if (tag != null && tag.getScope() != null && tag.getKey() != null)
            {
                tags.put(tag.getStorageKey(), tag);
            }
        }
    }

    public void save()
    {
        config_manager.setConfiguration(GROUP, KEY, gson.toJson(new ArrayList<>(tags.values())));
    }

    public List<EntityTag> getAll()
    {
        return new ArrayList<>(tags.values());
    }

    public Optional<EntityTag> findPlayer(String player_name)
    {
        if (player_name == null)
        {
            return Optional.empty();
        }

        return Optional.ofNullable(tags.get(EntityTagScope.PLAYER.name() + ":" + normalizePlayerName(player_name)));
    }

    public Optional<EntityTag> findNpc(int npc_id)
    {
        return Optional.ofNullable(tags.get(EntityTagScope.NPC.name() + ":" + npc_id));
    }

    public EntityTag touchPlayer(String player_name)
    {
        String normalized_name = normalizePlayerName(player_name);
        String storage_key = EntityTagScope.PLAYER.name() + ":" + normalized_name;

        EntityTag tag = tags.get(storage_key);
        if (tag == null)
        {
            tag = EntityTag.player(player_name);
        }
        else
        {
            tag.setDisplayName(player_name);
        }

        tags.put(storage_key, tag);
        save();
        return tag;
    }

    public EntityTag touchNpc(int npc_id, String npc_name)
    {
        String storage_key = EntityTagScope.NPC.name() + ":" + npc_id;

        EntityTag tag = tags.get(storage_key);
        if (tag == null)
        {
            tag = EntityTag.npc(npc_id, npc_name);
        }
        else
        {
            tag.setDisplayName(npc_name == null ? ("NPC " + npc_id) : npc_name);
        }

        tags.put(storage_key, tag);
        save();
        return tag;
    }

    public void upsert(EntityTag tag)
    {
        if (tag == null)
        {
            return;
        }

        tags.put(tag.getStorageKey(), tag);
        save();
    }

    public void delete(EntityTag tag)
    {
        if (tag == null)
        {
            return;
        }

        tags.remove(tag.getStorageKey());
        save();
    }

    public static String normalizePlayerName(String name)
    {
        return name.trim().toLowerCase(Locale.ROOT);
    }

    public EntityTag touchObject(int id, String name)
    {
        String key = "OBJ:" + id;

        EntityTag tag = tags.get(key);
        if (tag == null)
        {
            tag = new EntityTag(EntityTagScope.OBJECT, key, name, name, new Color(0x00FFFF));
        }

        tags.put(key, tag);
        save();
        return tag;
    }
}