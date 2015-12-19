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
package forestry.food.items;

import java.awt.Color;

import forestry.api.core.IModelManager;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumBeverage implements ItemBeverage.IBeverageInfo {
	MEAD_SHORT("meadShort", "glass", new Color(0xec9a19), new Color(0xffffff), 1, 0.2f, true),
	MEAD_CURATIVE("meadCurative", "glass", new Color(0xc5feff), new Color(0xffffff), 1, 0.2f, true);

	public static final EnumBeverage[] VALUES = values();

	private final String name;
	private final String iconType;
	private final int primaryColor;
	private final int secondaryColor;

	private final int heal;
	private final float saturation;
	private final boolean isAlwaysEdible;

	EnumBeverage(String name, String iconType, Color primaryColor, Color secondaryColor, int heal, float saturation, boolean isAlwaysEdible) {
		this.name = name;
		this.iconType = iconType;
		this.primaryColor = primaryColor.getRGB();
		this.secondaryColor = secondaryColor.getRGB();
		this.heal = heal;
		this.saturation = saturation;
		this.isAlwaysEdible = isAlwaysEdible;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager) {
		manager.registerItemModel(item, ordinal(), name);
	}

	@Override
	public int getHeal() {
		return heal;
	}

	@Override
	public float getSaturation() {
		return saturation;
	}

	@Override
	public boolean isAlwaysEdible() {
		return isAlwaysEdible;
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
}
