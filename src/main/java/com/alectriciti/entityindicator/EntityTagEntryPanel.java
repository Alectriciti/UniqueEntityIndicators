package com.alectriciti.entityindicator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.runelite.client.ui.ColorScheme;

public class EntityTagEntryPanel extends JPanel
{
    private final EntityTagStore tag_store;
    private final EntityTag tag;
    private final Runnable delete_refresh;

    private final JTextField label_field = new JTextField(16);
    private final JCheckBox enabled_box = new JCheckBox("On");
    private final JCheckBox text_box = new JCheckBox("Text");
    private final JCheckBox tile_box = new JCheckBox("Tile");
    private final JButton color_button = new JButton(" ");
    private final JButton delete_button = new JButton("X");

    public EntityTagEntryPanel(EntityTag tag, EntityTagStore tag_store, Runnable delete_refresh)
    {
        this.tag = tag;
        this.tag_store = tag_store;
        this.delete_refresh = delete_refresh;

        setLayout(new BorderLayout(8, 0));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        setPreferredSize(new Dimension(0, 78));

        JPanel left_panel = new JPanel();
        left_panel.setOpaque(false);
        left_panel.setLayout(new javax.swing.BoxLayout(left_panel, javax.swing.BoxLayout.Y_AXIS));

        JLabel type_label = new JLabel(tag.getScope() == EntityTagScope.PLAYER ? "Player" : "NPC");
        type_label.setForeground(Color.WHITE);

        JLabel name_label = new JLabel(tag.getDisplayName());
        name_label.setForeground(Color.LIGHT_GRAY);

        JLabel key_label = new JLabel(tag.getKey());
        key_label.setForeground(Color.GRAY);

        left_panel.add(type_label);
        left_panel.add(name_label);
        left_panel.add(key_label);

        JPanel center_panel = new JPanel();
        center_panel.setOpaque(false);
        center_panel.setLayout(new javax.swing.BoxLayout(center_panel, javax.swing.BoxLayout.Y_AXIS));

        JPanel label_row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        label_row.setOpaque(false);

        label_field.setText(tag.getLabel());
        label_field.setPreferredSize(new Dimension(160, 24));

        label_field.addActionListener(e -> saveOrDelete());
        label_field.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveOrDelete();
            }
        });

        label_row.add(new JLabel("Label"));
        label_row.add(label_field);

        JPanel toggles_row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        toggles_row.setOpaque(false);

        enabled_box.setSelected(tag.isEnabled());
        text_box.setSelected(tag.getDisplayMode().showsText());
        tile_box.setSelected(tag.getDisplayMode().showsTile());

        enabled_box.setToolTipText("Show or hide this tag");
        text_box.setToolTipText("Show overhead text");
        tile_box.setToolTipText("Show tile square");

        enabled_box.setOpaque(false);
        text_box.setOpaque(false);
        tile_box.setOpaque(false);

        enabled_box.addActionListener(e -> saveStateOnly());
        text_box.addActionListener(e -> updateDisplayMode());
        tile_box.addActionListener(e -> updateDisplayMode());

        toggles_row.add(enabled_box);
        toggles_row.add(text_box);
        toggles_row.add(tile_box);

        center_panel.add(label_row);
        center_panel.add(toggles_row);

        JPanel right_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        right_panel.setOpaque(false);

        color_button.setPreferredSize(new Dimension(24, 24));
        color_button.setMinimumSize(new Dimension(24, 24));
        color_button.setMaximumSize(new Dimension(24, 24));
        color_button.setBackground(tag.getColor());
        color_button.setOpaque(true);
        color_button.setBorderPainted(true);
        color_button.setFocusPainted(false);
        color_button.setToolTipText("Choose tag color");

        color_button.addActionListener(e ->
        {
            Color chosen_color = JColorChooser.showDialog(this, "Choose tag color", tag.getColor());
            if (chosen_color == null)
            {
                return;
            }

            tag.setColor(chosen_color);
            tag_store.upsert(tag);
        });

        delete_button.setPreferredSize(new Dimension(24, 24));
        delete_button.setFocusPainted(false);
        delete_button.setToolTipText("Delete tag");
        delete_button.addActionListener(e -> deleteTag());

        right_panel.add(color_button);
        right_panel.add(delete_button);

        add(left_panel, BorderLayout.WEST);
        add(center_panel, BorderLayout.CENTER);
        add(right_panel, BorderLayout.EAST);
    }

    private void saveStateOnly()
    {
        tag.setEnabled(enabled_box.isSelected());
        tag_store.upsert(tag);
    }

    private void updateDisplayMode()
    {
        boolean show_text = text_box.isSelected();
        boolean show_tile = tile_box.isSelected();

        if (!show_text && !show_tile)
        {
            text_box.setSelected(true);
            show_text = true;
        }

        if (show_text && show_tile)
        {
            tag.setDisplayMode(EntityTagDisplayMode.BOTH);
        }
        else if (show_text)
        {
            tag.setDisplayMode(EntityTagDisplayMode.TEXT);
        }
        else
        {
            tag.setDisplayMode(EntityTagDisplayMode.TILE);
        }

        tag_store.upsert(tag);
    }

    private void saveOrDelete()
    {
        String text = label_field.getText() == null ? "" : label_field.getText().trim();

        if (text.isEmpty())
        {
            deleteTag();
            return;
        }

        tag.setLabel(text);
        tag.setEnabled(enabled_box.isSelected());

        if (!text_box.isSelected() && !tile_box.isSelected())
        {
            text_box.setSelected(true);
            tag.setDisplayMode(EntityTagDisplayMode.TEXT);
        }
        else if (text_box.isSelected() && tile_box.isSelected())
        {
            tag.setDisplayMode(EntityTagDisplayMode.BOTH);
        }
        else if (text_box.isSelected())
        {
            tag.setDisplayMode(EntityTagDisplayMode.TEXT);
        }
        else
        {
            tag.setDisplayMode(EntityTagDisplayMode.TILE);
        }

        tag_store.upsert(tag);
    }

    private void deleteTag()
    {
        tag_store.delete(tag);
        delete_refresh.run();
    }
}