/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Locale;
import java.util.Random;

public enum EnumForestryWoodType implements IWoodType {
	LARCH(4),
	TEAK(4),
	ACACIA(4),
	LIME(4),
	CHESTNUT(4),
	WENGE(4),
	BAOBAB(4),
	SEQUOIA(3, 4.0f),

	KAPOK(4),
	EBONY(4),
	MAHOGANY(4),
	BALSA(4, 1.0f),
	WILLOW(4),
	WALNUT(4),
	GREENHEART(4, 7.5f),
	CHERRY(4),

	MAHOE(4),
	POPLAR(4),
	PALM(4),
	PAPAYA(4),
	PINE(4, 3.0f),
	PLUM(4),
	MAPLE(4),
	CITRUS(4),

	GIGANTEUM(3, 4.0f),
	IPE(4),
	PADAUK(4),
	COCOBOLO(4),
	ZEBRAWOOD(4);

	public static final float DEFAULT_HARDNESS = 2.0f;
	public static final EnumForestryWoodType[] VALUES = values();

	private final int carbonization;
	private final float hardness;

	EnumForestryWoodType(int carbonization) {
		this(carbonization, DEFAULT_HARDNESS);
	}

	EnumForestryWoodType(int carbonization, float hardness) {
		this.carbonization = carbonization;
		this.hardness = hardness;
	}

	@Override
	public float getHardness() {
		return hardness;
	}

	public static EnumForestryWoodType getRandom(Random random) {
		return VALUES[random.nextInt(VALUES.length)];
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public int getMetadata() {
		return ordinal();
	}

	public static EnumForestryWoodType byMetadata(int meta) {
		if (meta < 0 || meta >= VALUES.length) {
			meta = 0;
		}
		return VALUES[meta];
	}

	@Override
	public int getCarbonization() {
		return carbonization;
	}

	@Override
	public float getCharcoalChance(int numberOfCharcoal) {
		if (numberOfCharcoal == 3) {
			return 0.75F;
		} else if (numberOfCharcoal == 4) {
			return 0.5F;
		} else if (numberOfCharcoal == 5) {
			return 0.25F;
		}
		return 0.15F;
	}

	@Override
	public String getPlankTexture() {
		return "forestry:blocks/wood/planks." + getName();
	}

	@Override
	public String getDoorLowerTexture() {
		return "forestry:blocks/doors/" + getName() + "_lower";
	}

	@Override
	public String getDoorUpperTexture() {
		return "forestry:blocks/doors/" + getName() + "_upper";
	}

	@Override
	public String getBarkTexture() {
		return "forestry:blocks/wood/bark." + getName();
	}

	@Override
	public String getHeartTexture() {
		return "forestry:blocks/wood/heart." + getName();
	}
}
