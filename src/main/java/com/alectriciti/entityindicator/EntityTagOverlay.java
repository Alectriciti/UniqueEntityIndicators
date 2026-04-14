package com.alectriciti.entityindicator;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
public class EntityTagOverlay extends Overlay
{
    private final Client client;
    private final EntityTagStore tag_store;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    public EntityTagOverlay(Client client, EntityTagStore tag_store, ModelOutlineRenderer modelOutlineRenderer)
    {
        this.client = client;
        this.tag_store = tag_store;
        this.modelOutlineRenderer = modelOutlineRenderer;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return null;
        }

        WorldView top_world_view = client.getTopLevelWorldView();
        if (top_world_view == null)
        {
            return null;
        }

        renderPlayers(graphics, top_world_view);

        Player local_player = client.getLocalPlayer();
        if (local_player != null && local_player.getWorldView() != null && local_player.getWorldView() != top_world_view)
        {
            renderPlayers(graphics, local_player.getWorldView());
        }

        renderNpcs(graphics, top_world_view);
        return null;
    }

    private void renderOutline(Graphics2D g, Actor actor, Color color)
    {
//        java.awt.Shape hull = actor.

        int width = 4;
        int feather =4;
        modelOutlineRenderer.drawOutline(actor, width, color, feather);

//        if (hull == null)
//        {

//            return;
//        }

//        g.setColor(color);
//        g.setStroke(new BasicStroke(2));
//        g.draw(hull);
    }

    private void renderPlayers(Graphics2D graphics, WorldView world_view)
    {
        for (Player player : world_view.players())
        {
            if (player == null || player.getName() == null)
            {
                continue;
            }

            tag_store.findPlayer(player.getName())
                    .filter(EntityTag::isEnabled)
                    .ifPresent(tag -> renderActor(graphics, player, tag));
        }
    }

    private void renderNpcs(Graphics2D graphics, WorldView world_view)
    {
        for (NPC npc : world_view.npcs())
        {
            if (npc == null)
            {
                continue;
            }

            tag_store.findNpc(npc.getId())
                    .filter(EntityTag::isEnabled)
                    .ifPresent(tag -> renderActor(graphics, npc, tag));
        }
    }

    private void renderActor(Graphics2D graphics, Actor actor, EntityTag tag)
    {
        if (tag.getDisplayMode().showsTile())
        {
            Polygon poly = actor.getCanvasTilePoly();
            if (poly != null)
            {
                OverlayUtil.renderPolygon(graphics, poly, tag.getColor());
            }
        }

        if (tag.getDisplayMode().showsOutline())
        {
            renderOutline(graphics, actor, tag.getColor());
        }

        if (tag.getDisplayMode().showsText())
        {
            Point text_location = actor.getCanvasTextLocation(graphics, tag.getLabel(), actor.getLogicalHeight() + 40);
            if (text_location != null)
            {
                OverlayUtil.renderTextLocation(graphics, text_location, tag.getLabel(), tag.getColor());
            }
        }
    }


}