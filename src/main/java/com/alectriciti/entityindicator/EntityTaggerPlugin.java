package com.alectriciti.entityindicator;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import java.awt.*;

@Slf4j
@PluginDescriptor(
        name = "Entity Tagger",
        description = "Tag players and NPCs from sidebar panel"
)
public class EntityTaggerPlugin extends Plugin
{
    private static final String TAG_PLAYER = "Tag player";
    private static final String TAG_NPC = "Tag NPC";

    @Inject private Client client;
    @Inject private ClientThread client_thread;
    @Inject private ClientToolbar client_toolbar;
    @Inject private OverlayManager overlay_manager;

    @Inject private EntityTagStore tag_store;
    @Inject private EntityTaggerPanel panel;
    @Inject private EntityTagOverlay overlay;

    private NavigationButton nav_button;

    @Provides
    EntityTaggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(EntityTaggerConfig.class);
    }

    @Override
    protected void startUp()
    {
        tag_store.load();
        overlay_manager.add(overlay);

        nav_button = NavigationButton.builder()
                .tooltip("Entity Tagger")
                .icon(ImageUtil.loadImageResource(EntityTaggerPlugin.class, "entity_tagger_icon.png"))
                .panel(panel)
                .priority(10)
                .build();

        client_toolbar.addNavigation(nav_button);
        panel.refresh();
    }

    @Override
    protected void shutDown()
    {
        overlay_manager.remove(overlay);

        if (nav_button != null)
        {
            client_toolbar.removeNavigation(nav_button);
            nav_button = null;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (!client.isKeyPressed(KeyCode.KC_CONTROL))
        {
            return;
        }

        MenuEntry entry = event.getMenuEntry();

        // prevent duplicates (VERY important)
        if (entry.getOption().equals("Create Tag"))
        {
            return;
        }

        final Player player = entry.getPlayer();
        final NPC npc = entry.getNpc();

        if (player == null && npc == null)
        {
            return;
        }

        final boolean is_player = player != null;

        final String target = entry.getTarget();
        final int npc_id = npc != null ? npc.getId() : -1;
        final String player_name = player != null ? player.getName() : null;
        final String npc_name_raw = npc != null ? npc.getName() : null;

        client.createMenuEntry(-1)
                .setOption("Create Tag")
                .setTarget(target)
                .setType(MenuAction.RUNELITE)
                .setWorldViewId(entry.getWorldViewId())
                .onClick(e ->
                        client_thread.invokeLater(() ->
                        {
                            if (is_player)
                            {
                                tag_store.touchPlayer(player_name);
                            }
                            else
                            {
                                String clean = RuneTextUtil.stripAllTags(npc_name_raw);
                                Color parsed = RuneTextUtil.extractColor(npc_name_raw);

                                EntityTag tag = tag_store.touchNpc(npc_id, clean);

                                if (parsed != null)
                                {
                                    tag.setColor(parsed);
                                    tag_store.upsert(tag);
                                }
                            }

                            panel.refresh();
                            client_toolbar.openPanel(nav_button);
                        })
                );
    }
}