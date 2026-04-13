package com.alectriciti.entityindicator;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EntityTaggerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(EntityTaggerPlugin.class);
		RuneLite.main(args);
	}
}