package com.alectriciti.entityindicator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.inject.Inject;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class EntityTaggerPanel extends PluginPanel
{
    private final EntityTagStore tag_store;

    private final JSplitPane split_pane;
    private final JPanel player_list_panel = new JPanel();
    private final JPanel npc_list_panel = new JPanel();

    @Inject
    public EntityTaggerPanel(EntityTagStore tag_store)
    {
        this.tag_store = tag_store;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel top_container = new JPanel();
        top_container.setLayout(new BoxLayout(top_container, BoxLayout.Y_AXIS));
        top_container.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel button_row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        button_row.setBackground(ColorScheme.DARK_GRAY_COLOR);

        javax.swing.JButton import_btn = new javax.swing.JButton("Import");
        javax.swing.JButton export_btn = new javax.swing.JButton("Export");
        javax.swing.JButton clear_btn = new javax.swing.JButton("Clear");

        import_btn.setToolTipText("Imports from your Clipboard");
        export_btn.setToolTipText("Exports to your Clipboard");
        clear_btn.setToolTipText("Clears the Tag List");

        import_btn.addActionListener(e ->
        {
            String data = getClipboard();
            if (data == null || data.isEmpty())
            {
                return;
            }

            tag_store.importFromString(data);
            refresh();
        });

        export_btn.addActionListener(e ->
        {
            String data = tag_store.exportToString();
            setClipboard(data);
        });

        clear_btn.addActionListener(e ->
        {
            int result = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Clear all tags?",
                    "Confirm",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (result == javax.swing.JOptionPane.YES_OPTION)
            {
                tag_store.clear();
                refresh();
            }
        });

        button_row.add(import_btn);
        button_row.add(export_btn);
        button_row.add(clear_btn);

        top_container.add(button_row);
        add(top_container, BorderLayout.NORTH);

        player_list_panel.setLayout(new BoxLayout(player_list_panel, BoxLayout.Y_AXIS));
        player_list_panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        npc_list_panel.setLayout(new BoxLayout(npc_list_panel, BoxLayout.Y_AXIS));
        npc_list_panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JScrollPane player_scroll_pane = new JScrollPane(player_list_panel);
        player_scroll_pane.setBorder(null);

        JScrollPane npc_scroll_pane = new JScrollPane(npc_list_panel);
        npc_scroll_pane.setBorder(null);

        split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, player_scroll_pane, npc_scroll_pane);

        split_pane.setDividerSize(12);
        split_pane.setResizeWeight(0.5);
        split_pane.setContinuousLayout(true);
        split_pane.setBorder(null);
        split_pane.setDividerLocation(0.5);

        add(split_pane, BorderLayout.CENTER);

        // Ensure divider is centered initially
//        java.awt.EventQueue.invokeLater(() -> split_pane.setDividerLocation(0.5));

        refresh();
    }

    private String getClipboard()
    {
        try
        {
            java.awt.datatransfer.Clipboard clipboard =
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();

            return (String) clipboard.getData(java.awt.datatransfer.DataFlavor.stringFlavor);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private void setClipboard(String text)
    {
        try
        {
            java.awt.datatransfer.StringSelection selection =
                    new java.awt.datatransfer.StringSelection(text);

            java.awt.Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(selection, null);
        }
        catch (Exception ignored) {}
    }

    public void refresh()
    {
        split_pane.setDividerSize(8);
        split_pane.setResizeWeight(0.5);

        split_pane.repaint();
        split_pane.revalidate();


        player_list_panel.removeAll();
        npc_list_panel.removeAll();

        List<EntityTag> tags = tag_store.getAll();
        for (EntityTag tag : tags)
        {
            if (tag == null || tag.getScope() == null) continue;

            switch(tag.getScope()){
                case PLAYER:
                    player_list_panel.add(new EntityTagEntryPanel(tag, tag_store, this::refresh));
                    break;
                case NPC:
                    npc_list_panel.add(new EntityTagEntryPanel(tag, tag_store, this::refresh));
                    break;
                case OBJECT:
                    break;
            }
        }

        player_list_panel.revalidate();
        player_list_panel.repaint();

        npc_list_panel.revalidate();
        npc_list_panel.repaint();
    }


}