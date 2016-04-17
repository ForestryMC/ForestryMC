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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;

public enum EnumBeverage implements ItemBeverage.IBeverageInfo {
	MEAD_SHORT("meadShort", "glass", new Color(0xec9a19), new Color(0xffffff), 1, 0.2f, true),
	MEAD_CURATIVE("meadCurative", "glass", new Color(0xc5feff), new Color(0xffffff), 1, 0.2f, true),
	MEAD("mead", "glass", new Color(0xcc6600), new Color(0xffffff), 10, 0.1f, true);
	public static final EnumBeverage[] VALUES = values();

	private final String name;
	private final String iconType;
	private final int primaryColor;
	private final int secondaryColor;

	@SideOnly(Side.CLIENT)
	private IIcon iconBottle;
	@SideOnly(Side.CLIENT)
	private IIcon iconContents;

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
	public void registerIcons(IIconRegister register) {
		iconBottle = TextureManager.registerTex(register, "liquids/" + iconType + ".bottle");
		iconContents = TextureManager.registerTex(register, "liquids/" + iconType + ".contents");
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

	@Override
	public IIcon getIconBottle() {
		return iconBottle;
	}

	@Override
	public IIcon getIconContents() {
		return iconContents;
	}
}
