package forestry.api.arboriculture;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockPlanks;

public enum EnumVanillaWoodType implements IWoodType {
	OAK(BlockPlanks.EnumType.OAK),
	BIRCH(BlockPlanks.EnumType.BIRCH),
	SPRUCE(BlockPlanks.EnumType.SPRUCE),
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

	@Nonnull
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
}
