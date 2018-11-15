package forestry.apiculture.blocks;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum BlockBeeProductType implements IStringSerializable {
	BEE_COMBS,
	BEESWAX;

	public static final BlockBeeProductType[] VALUES = values();

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public int getMeta() {
		return ordinal();
	}

}
