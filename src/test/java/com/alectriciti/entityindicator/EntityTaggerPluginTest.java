package com.alectriciti.entityindicator;

import com.HeroHud.HeroHudMain;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EntityTaggerPluginTest
{
	public static void main(String[] args) throws Exception
	{
        ExternalPluginManager.loadBuiltin(
                EntityTaggerPlugin.class,
                HeroHudMain.class);
		RuneLite.main(args);
	}
}