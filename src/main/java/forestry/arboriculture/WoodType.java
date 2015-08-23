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

import java.util.Locale;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;

public enum WoodType {
	LARCH(0), TEAK(1), ACACIA(2), LIME(3),
	CHESTNUT(0), WENGE(1), BAOBAB(2), SEQUOIA(4.0f, 3),
	KAPOK(0), EBONY(1), MAHOGANY(2), BALSA(1.0f, 3),
	WILLOW(0), WALNUT(1), GREENHEART(7.5f, 2), CHERRY(3),
	MAHOE(0), POPLAR(1), PALM(2), PAPAYA(3),
	PINE(3.0f, 0), PLUM(1), MAPLE(2), CITRUS(3),
	GIGANTEUM(2.0f, 0), IPE(1), PADAUK(2), COCOBOLO(3),
	ZEBRAWOOD(0);

	public static final WoodType[] VALUES = values();
	private final float hardness;
	private final int meta;

	private WoodType(int meta) {
		this(2.0f, meta);
	}

	private WoodType(float hardness, int meta) {
		this.hardness = hardness;
		this.meta = meta;
	}
	
	public int getMeta() {
		return meta;
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
			if (typeOrdinal < WoodType.VALUES.length) {
				return WoodType.VALUES[typeOrdinal];
			}
		}

		return WoodType.LARCH;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

}
