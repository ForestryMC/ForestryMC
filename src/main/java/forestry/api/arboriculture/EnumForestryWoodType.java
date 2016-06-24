/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public enum EnumForestryWoodType implements IWoodType {
	LARCH(4, 20),
	TEAK(4, 20),
	ACACIA(4, 20),
	LIME(4, 19),
	CHESTNUT(4, 21),
	WENGE(4, 20),
	BAOBAB(4, 20),
	SEQUOIA(3, 18, 4.0f),

	KAPOK(4, 20),
	EBONY(4, 20),
	MAHOGANY(4, 20),
	BALSA(4, 20, 1.0f),
	WILLOW(4, 20),
	WALNUT(4, 21),
	GREENHEART(4, 20, 7.5f),
	CHERRY(4, 20),

	MAHOE(4, 20),
	POPLAR(4, 20),
	PALM(4, 20),
	PAPAYA(4, 20),
	PINE(4, 20, 3.0f),
	PLUM(4, 20),
	MAPLE(4, 20),
	CITRUS(4, 20),

	GIGANTEUM(3, 18, 4.0f),
	IPE(4, 20),
	PADAUK(4, 20),
	COCOBOLO(4, 20),
	ZEBRAWOOD(4, 20);

	public static final float DEFAULT_HARDNESS = 2.0f;
	public static final EnumForestryWoodType[] VALUES = values();

	private final int carbonization;
	private final int combustability;
	private final float hardness;

	EnumForestryWoodType(int carbonization, int combustability) {
		this(carbonization, combustability, DEFAULT_HARDNESS);
	}

	EnumForestryWoodType(int carbonization, int combustability, float hardness) {
		this.carbonization = carbonization;
		this.combustability = combustability;
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

	@Nonnull
	public static EnumForestryWoodType byMetadata(int meta) {
		if (meta < 0 || meta >= VALUES.length) {
			meta = 0;
		}
		return VALUES[meta];
	}

	public int getCarbonization() {
		return carbonization;
	}

	public int getCombustability() {
		return combustability;
	}
}
