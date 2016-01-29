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
package forestry.arboriculture.render;

import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.core.render.TextureManager;

public abstract class IconProviderWood {
	@SideOnly(Side.CLIENT)
	private static IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	public static void registerIcons(IIconRegister register) {
		icons = new IIcon[3][EnumWoodType.VALUES.length];
		for (int i = 0; i < EnumWoodType.VALUES.length; i++) {
			EnumWoodType woodType = EnumWoodType.VALUES[i];
			String woodName = woodType.toString().toLowerCase(Locale.ENGLISH);

			icons[0][i] = TextureManager.registerTex(register, "wood/planks." + woodName);
			icons[1][i] = TextureManager.registerTex(register, "wood/bark." + woodName);
			icons[2][i] = TextureManager.registerTex(register, "wood/heart." + woodName);
		}
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getPlankIcon(EnumWoodType woodType) {
		if (woodType == null) {
			woodType =  EnumWoodType.LARCH;
		}
		return icons[0][woodType.ordinal()];
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getBarkIcon(EnumWoodType woodType) {
		if (woodType == null) {
			woodType =  EnumWoodType.LARCH;
		}
		return icons[1][woodType.ordinal()];
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getHeartIcon(EnumWoodType woodType) {
		if (woodType == null) {
			woodType =  EnumWoodType.LARCH;
		}
		return icons[2][woodType.ordinal()];
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getLogIcon(EnumWoodType woodType, int meta, int side) {
		int oriented = meta & 12;
		switch (oriented) {
			case 4:
				if (side > 3) {
					return getHeartIcon(woodType);
				} else {
					return getBarkIcon(woodType);
				}
			case 8:
				if (side == 2 || side == 3) {
					return getHeartIcon(woodType);
				} else {
					return getBarkIcon(woodType);
				}
			case 0:
			default:
				if (side < 2) {
					return getHeartIcon(woodType);
				} else {
					return getBarkIcon(woodType);
				}
		}
	}
}
