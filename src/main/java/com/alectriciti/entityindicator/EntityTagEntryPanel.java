package com.alectriciti.entityindicator;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class EntityTagEntryPanel extends JPanel
{
    private static final int CARD_HEIGHT = 84;
    private static final int LABEL_WIDTH = 104;
    private static final int BUTTON_SIZE = 22;
    private static final int GAP = 4;

    private final EntityTagStore tag_store;
    private final EntityTag tag;
    private final Runnable delete_refresh;

    private final JTextField label_field = new JTextField(12);
    private final JCheckBox name_box = new JCheckBox("Name");
    private final JCheckBox tile_box = new JCheckBox("Floor");
    private final JCheckBox outline_box = new JCheckBox("Outline");
    private final JButton color_button = new JButton(" ");
    private final JButton delete_button = new JButton("X");

    private boolean hovered = false;
    private boolean selected = false;


    public EntityTagEntryPanel(EntityTag tag, EntityTagStore tag_store, Runnable delete_refresh)
    {
        this.tag = tag;
        this.tag_store = tag_store;
        this.delete_refresh = delete_refresh;


        outline_box.setSelected(tag.getDisplayMode().showsOutline());
        outline_box.setOpaque(false);
        outline_box.setToolTipText("Highlight entity outline");

        outline_box.addActionListener(e -> updateDisplayMode());

        setLayout(new BorderLayout(GAP, 0));
        setBackground(deriveBackground(tag.getColor()));
        updateBorder();
        setMaximumSize(new Dimension(Integer.MAX_VALUE, CARD_HEIGHT));
        setPreferredSize(new Dimension(0, CARD_HEIGHT));
        setMinimumSize(new Dimension(0, CARD_HEIGHT));
        setAlignmentX(LEFT_ALIGNMENT);

        JPanel left_panel = new JPanel();
        left_panel.setOpaque(false);
        left_panel.setLayout(new javax.swing.BoxLayout(left_panel, javax.swing.BoxLayout.Y_AXIS));

        JLabel type_label = new JLabel(tag.getScope() == EntityTagScope.PLAYER ? "Player" : "NPC");
        type_label.setForeground(Color.WHITE);
        type_label.setFont(type_label.getFont().deriveFont(Font.BOLD, 12f));

//        JLabel name_label = new JLabel(tag.getDisplayName());
//        name_label.setForeground(Color.LIGHT_GRAY);

        JLabel key_label = new JLabel(tag.getKey());
        key_label.setForeground(Color.GRAY);
        key_label.setFont(key_label.getFont().deriveFont(10f));

        left_panel.add(type_label);
//        left_panel.add(name_label);
        left_panel.add(key_label);

        JPanel center_panel = new JPanel();
        center_panel.setOpaque(false);
        center_panel.setLayout(new javax.swing.BoxLayout(center_panel, javax.swing.BoxLayout.Y_AXIS));

        JPanel label_row = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, 0));
        label_row.setOpaque(false);

        label_field.setText(tag.getLabel());
        label_field.setPreferredSize(new Dimension(LABEL_WIDTH, 24));
        label_field.setMaximumSize(new Dimension(LABEL_WIDTH, 24));

        label_field.addActionListener(e -> saveOrDelete());
        label_field.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveOrDelete();
            }
        });

        JLabel title = new JLabel(tag.getDisplayName());
        title.setForeground(Color.DARK_GRAY);
        label_row.add(title);
        label_row.add(label_field);

        JPanel toggle_row = new JPanel(new GridLayout(1, 4, GAP, 0));
        toggle_row.setOpaque(false);
        toggle_row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        toggle_row.setOpaque(false);

        name_box.setSelected(tag.getDisplayMode().showsText());
        outline_box.setSelected(tag.getDisplayMode().showsOutline());
        tile_box.setSelected(tag.getDisplayMode().showsTile());

        name_box.setToolTipText("Show overhead name");
        outline_box.setToolTipText("Show outline");
        tile_box.setToolTipText("Show floor tile");

        name_box.setOpaque(false);
        outline_box.setOpaque(false);
        tile_box.setOpaque(false);

        name_box.addActionListener(e -> updateDisplayMode());
        outline_box.addActionListener(e -> updateDisplayMode());
        tile_box.addActionListener(e -> updateDisplayMode());

        toggle_row.add(name_box);
        toggle_row.add(outline_box);
        toggle_row.add(tile_box);

        center_panel.add(label_row);
        center_panel.add(toggle_row);

        JPanel right_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        right_panel.setOpaque(false);

        color_button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        color_button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        color_button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        color_button.setBackground(tag.getColor());
        color_button.setOpaque(true);
        color_button.setBorderPainted(true);
        color_button.setFocusPainted(false);
        color_button.setContentAreaFilled(true);
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

            color_button.setBackground(chosen_color);

            // 🔥 update card color too
            setBackground(deriveBackground(chosen_color));

            repaint();
        });

        delete_button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        delete_button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        delete_button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        delete_button.setFocusPainted(false);
        delete_button.setToolTipText("Delete tag");
        delete_button.addActionListener(e -> deleteTag());

        right_panel.add(color_button);
        right_panel.add(delete_button);

        add(left_panel, BorderLayout.WEST);
        add(center_panel, BorderLayout.CENTER);
        add(right_panel, BorderLayout.EAST);

        updateBorder();

        addMouseListener(new MouseListener()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                hovered = true;
                updateBorder();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                hovered = false;
                updateBorder();
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                requestFocusInWindow(); // important for keyboard nav
                setSelected(true);
            }

            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
        });
    }

    public void setSelected(boolean selected)
    {

        this.selected = selected;
        updateBorder();
    }

    private void updateBorder()
    {
        Color border_color;

        if (selected)
        {
            border_color = tag.getColor(); // strong highlight
        }
        else if (hovered)
        {
            border_color = ColorScheme.PROGRESS_COMPLETE_COLOR; // RuneLite green-ish
        }
        else
        {
            border_color = ColorScheme.MEDIUM_GRAY_COLOR;
        }

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border_color, selected ? 2 : 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void saveStateOnly()
    {
//        tag.setEnabled(enabled_box.isSelected());
        tag_store.upsert(tag);
    }

    private void updateDisplayMode()
    {
        boolean text = name_box.isSelected();
        boolean tile = tile_box.isSelected();
        boolean outline = outline_box.isSelected();

        // prevent all-off state
        if (!text && !tile && !outline)
        {
            name_box.setSelected(true);
            text = true;
        }

        tag.setDisplayMode(EntityTagDisplayMode.from(text, tile, outline));
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
//        tag.setEnabled(enabled_box.isSelected());

        boolean btext = name_box.isSelected();
        boolean tile = tile_box.isSelected();
        boolean outline = outline_box.isSelected();

        if (!btext && !tile && !outline)
        {
            name_box.setSelected(true);
            btext = true;
        }

        tag.setDisplayMode(EntityTagDisplayMode.from(btext, tile, outline));

        tag_store.upsert(tag);
    }

    private void deleteTag()
    {
        tag_store.delete(tag);
        delete_refresh.run();
    }

    private Color deriveBackground(Color base)
    {
        // darken + blend with RuneLite background
        int r = (base.getRed() + 40) / 4;
        int g = (base.getGreen() + 40) / 4;
        int b = (base.getBlue() + 40) / 4;

        return new Color(r, g, b);
    }

}