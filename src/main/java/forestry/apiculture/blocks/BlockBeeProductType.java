package forestry.apiculture.blocks;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum BlockBeeProductType implements StringRepresentable {
	BEE_COMBS,
	BEESWAX;

	public static final BlockBeeProductType[] VALUES = values();

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public int getMeta() {
		return ordinal();
	}

}
