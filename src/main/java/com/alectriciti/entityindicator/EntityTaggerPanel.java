package com.alectriciti.entityindicator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.inject.Inject;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class EntityTaggerPanel extends PluginPanel
{
    private final EntityTagStore tag_store;
    private final JPanel list_panel = new JPanel();

    @Inject
    public EntityTaggerPanel(EntityTagStore tag_store)
    {
        this.tag_store = tag_store;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel top_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top_panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        top_panel.add(new JLabel("Ctrl + right-click players or NPCs to add them here."));
        add(top_panel, BorderLayout.NORTH);

        list_panel.setLayout(new BoxLayout(list_panel, BoxLayout.Y_AXIS));
        list_panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

//        JScrollPane scroll_pane = new JScrollPane(list_panel);
//        scroll_pane.setBorder(null);
//        add(scroll_pane, BorderLayout.CENTER);
        add(list_panel, BorderLayout.CENTER);
    }

    public void refresh()
    {
        list_panel.removeAll();

        List<EntityTag> tags = tag_store.getAll();
        for (EntityTag tag : tags)
        {
            list_panel.add(new EntityTagEntryPanel(tag, tag_store, this::refresh));
        }

        list_panel.revalidate();
        list_panel.repaint();
    }
}