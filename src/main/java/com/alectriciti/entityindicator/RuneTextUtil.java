package com.alectriciti.entityindicator;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RuneTextUtil
{
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern COLOR_PATTERN = Pattern.compile("<col=([0-9a-fA-F]{2,6})>");

    public static String stripAllTags(String input)
    {
        if (input == null)
        {
            return null;
        }

        return TAG_PATTERN.matcher(input).replaceAll("");
    }

    public static Color extractColor(String input)
    {
        if (input == null)
        {
            return null;
        }

        Matcher m = COLOR_PATTERN.matcher(input);
        if (m.find())
        {
            String hex = m.group(1);

            // normalize short hex (ff00 → 00ff00 style fix)
            if (hex.length() == 4)
            {
                hex = "00" + hex;
            }

            try
            {
                return new Color(Integer.parseInt(hex, 16));
            }
            catch (Exception ignored) {}
        }

        return null;
    }
}