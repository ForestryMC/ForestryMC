/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.mail.items;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.init.Items;

import forestry.api.mail.EnumPostage;
import forestry.core.items.ItemOverlay;
import forestry.plugins.PluginCore;

public enum EnumStampDefinition implements ItemOverlay.IOverlayInfo {
	P_1("1n", EnumPostage.P_1, "gemApatite", new Color(0x4a8ca7), new Color(0xffffff)),
	P_2("2n", EnumPostage.P_2, "ingotCopper", new Color(0xe8c814), new Color(0xffffff)),
	P_5("5n", EnumPostage.P_5, "ingotTin", new Color(0x9c0707), new Color(0xffffff)),
	P_10("10n", EnumPostage.P_10, "ingotGold", new Color(0x7bd1b8), new Color(0xffffff)),
	P_20("20n", EnumPostage.P_20, "gemDiamond", new Color(0xff9031), new Color(0xfff7dd)),
	P_50("50n", EnumPostage.P_50, "gemEmerald", new Color(0x6431d7), new Color(0xfff7dd)),
	P_100("100n", EnumPostage.P_100, Items.nether_star, new Color(0xd731ba), new Color(0xfff7dd)),
	//	P_200("200n", EnumPostage.P_200, Items.nether_star, new Color(0xcd9831), new Color(0xfff7dd)),
	;

	public static final EnumStampDefinition[] VALUES = values();
	private static final Map<EnumPostage, EnumStampDefinition> postageMap = new EnumMap<>(EnumPostage.class);

	static {
		for (EnumStampDefinition stampDefinition : VALUES) {
			postageMap.put(stampDefinition.getPostage(), stampDefinition);
		}
	}

	private final String name;
	private final int primaryColor;
	private final int secondaryColor;
	private final Object craftingIngredient;
	private final EnumPostage postage;

	EnumStampDefinition(String name, EnumPostage postage, Object crafting, Color primaryColor, Color secondaryColor) {
		this.name = name;
		this.primaryColor = primaryColor.getRGB();
		this.secondaryColor = secondaryColor.getRGB();
		this.craftingIngredient = crafting;
		this.postage = postage;
	}

	public EnumPostage getPostage() {
		return this.postage;
	}

	public Object getCraftingIngredient() {
		return this.craftingIngredient;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPrimaryColor() {
		return primaryColor;
	}

	@Override
	public int getSecondaryColor() {
		return secondaryColor;
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	public static EnumStampDefinition getFromPostage(EnumPostage postage) {
		return postageMap.get(postage);
	}
}
