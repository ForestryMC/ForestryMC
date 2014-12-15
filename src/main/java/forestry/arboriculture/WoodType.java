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
package forestry.arboriculture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.render.TextureManager;
import java.util.Locale;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public enum WoodType {
	LARCH, TEAK, ACACIA, LIME, CHESTNUT, WENGE, BAOBAB, SEQUOIA(4.0f), KAPOK, EBONY, MAHOGANY, BALSA(1.0f), WILLOW, WALNUT, GREENHEART(7.5f), CHERRY, MAHOE, POPLAR, PALM, PAPAYA, PINE(
			3.0f), PLUM, MAPLE, CITRUS, GIGANTEUM(2.0f, false);

	public static final WoodType[] VALUES = values();
	private final float hardness;
	public final boolean hasPlank;

	private WoodType() {
		this(2.0f, true);
	}

	private WoodType(float hardness) {
		this(hardness, true);
	}
	
	private WoodType(float hardness, boolean hasPlank) {
		this.hardness = hardness;
		this.hasPlank = hasPlank;
	}

	@SideOnly(Side.CLIENT)
	private static IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	public static void registerIcons(IIconRegister register) {
		icons = new IIcon[3][VALUES.length];
		for (int i = 0; i < VALUES.length; i++) {
			WoodType woodType = VALUES[i];
			String woodName = woodType.toString().toLowerCase(Locale.ENGLISH);

			if (woodType.hasPlank)
				icons[0][i] = TextureManager.getInstance().registerTex(register, "wood/planks." + woodName);
			else
				icons[0][i] = null;
			icons[1][i] = TextureManager.getInstance().registerTex(register, "wood/bark." + woodName);
			icons[2][i] = TextureManager.getInstance().registerTex(register, "wood/heart." + woodName);
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getPlankIcon() {
		return icons[0][ordinal()];
	}

	@SideOnly(Side.CLIENT)
	public IIcon getBarkIcon() {
		return icons[1][ordinal()];
	}

	@SideOnly(Side.CLIENT)
	public IIcon getHeartIcon() {
		return icons[2][ordinal()];
	}

	public float getHardness() {
		return hardness;
	}

	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("WoodType", this.ordinal());
	}

	public static WoodType getFromCompound(NBTTagCompound compound) {

		if (compound != null) {
			int typeOrdinal = compound.getInteger("WoodType");
			if (typeOrdinal < WoodType.VALUES.length)
				return WoodType.VALUES[typeOrdinal];
		}

		return WoodType.LARCH;
	}

}
