/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Locale;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

public enum EnumWoodType {
	LARCH, TEAK, ACACIA, LIME, CHESTNUT,
	WENGE, BAOBAB, SEQUOIA(4.0f), KAPOK, EBONY,
	MAHOGANY, BALSA(1.0f), WILLOW, WALNUT, GREENHEART(7.5f),
	CHERRY, MAHOE, POPLAR, PALM, PAPAYA,
	PINE(3.0f), PLUM, MAPLE, CITRUS, GIGANTEUM,
	IPE, PADAUK, COCOBOLO, ZEBRAWOOD;

	public static final float DEFAULT_HARDNESS = 2.0f;
	public static final EnumWoodType[] VALUES = values();

	private final float hardness;

	EnumWoodType() {
		this.hardness = DEFAULT_HARDNESS;
	}

	EnumWoodType(float hardness) {
		this.hardness = hardness;
	}

	public float getHardness() {
		return hardness;
	}

	/**
	 * @deprecated since Forestry 4.2. Woods use itemDamage instead of NBT now
	 */
	@Deprecated
	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("WoodType", ordinal());
	}

	/**
	 * @deprecated since Forestry 4.2. Woods use itemDamage instead of NBT now
	 */
	@Deprecated
	public static EnumWoodType getFromCompound(NBTTagCompound compound) {
		if (compound != null) {
			int typeOrdinal = compound.getInteger("WoodType");
			if (typeOrdinal >= 0 && typeOrdinal < VALUES.length) {
				return VALUES[typeOrdinal];
			}
		}

		return EnumWoodType.LARCH;
	}

	public static EnumWoodType getRandom(Random random) {
		return VALUES[random.nextInt(VALUES.length)];
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}
}
