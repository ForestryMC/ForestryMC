package forestry.api.arboriculture;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockPlanks;

public enum EnumVanillaWoodType implements IWoodType {
	OAK(BlockPlanks.EnumType.OAK, 5, 20),
	SPRUCE(BlockPlanks.EnumType.SPRUCE, 4, 19),
	BIRCH(BlockPlanks.EnumType.BIRCH, 4, 19),
	JUNGLE(BlockPlanks.EnumType.JUNGLE, 5, 20),
	ACACIA(BlockPlanks.EnumType.ACACIA, 4, 20),
	DARK_OAK(BlockPlanks.EnumType.DARK_OAK, 5, 20);

	public static final EnumVanillaWoodType[] VALUES = values();

	public static EnumVanillaWoodType getRandom(Random random) {
		return VALUES[random.nextInt(VALUES.length)];
	}

	private final BlockPlanks.EnumType vanillaType;
	private final int carbonization;
	private final int combustability;

	EnumVanillaWoodType(BlockPlanks.EnumType vanillaType, int carbonization, int combustability) {
		this.vanillaType = vanillaType;
		this.carbonization = carbonization;
		this.combustability = combustability;
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

	@Override
	public int getCarbonization() {
		return carbonization;
	}

	@Override
	public int getCombustability() {
		return combustability;
	}
}
