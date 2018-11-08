/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockPlanks;

public enum EnumVanillaWoodType implements IWoodType {
	OAK(BlockPlanks.EnumType.OAK),
	SPRUCE(BlockPlanks.EnumType.SPRUCE),
	BIRCH(BlockPlanks.EnumType.BIRCH),
	JUNGLE(BlockPlanks.EnumType.JUNGLE),
	ACACIA(BlockPlanks.EnumType.ACACIA),
	DARK_OAK(BlockPlanks.EnumType.DARK_OAK);

	public static final EnumVanillaWoodType[] VALUES = values();

	public static EnumVanillaWoodType getRandom(Random random) {
		return VALUES[random.nextInt(VALUES.length)];
	}

	private final BlockPlanks.EnumType vanillaType;

	EnumVanillaWoodType(BlockPlanks.EnumType vanillaType) {
		this.vanillaType = vanillaType;
	}

	public BlockPlanks.EnumType getVanillaType() {
		return vanillaType;
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

	public static EnumVanillaWoodType byMetadata(int meta) {
		if (meta < 0 || meta >= VALUES.length) {
			meta = 0;
		}
		return VALUES[meta];
	}

	@Override
	public float getHardness() {
		return 2.0F;
	}

	@Override
	public String getPlankTexture() {
		if (this == DARK_OAK) {
			return "blocks/planks_big_oak";
		}
		return "blocks/planks_" + getName();
	}

	@Override
	public String getDoorLowerTexture() {
		if (this == OAK) {
			return "blocks/door_wood_lower";
		}
		return "blocks/door_wood_lower";
	}

	@Override
	public String getDoorUpperTexture() {
		if (this == OAK) {
			return "blocks/door_wood_upper";
		}
		return "blocks/door_" + getName() + "_upper";
	}

	@Override
	public String getBarkTexture() {
		if (this == DARK_OAK) {
			return "blocks/log_big_oak";
		}
		return "blocks/log_" + getName();
	}

	@Override
	public String getHeartTexture() {
		if (this == DARK_OAK) {
			return "blocks/log_big_oak_top";
		}
		return "blocks/log_" + getName() + "_top";
	}
}
