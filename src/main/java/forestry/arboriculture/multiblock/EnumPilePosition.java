package forestry.arboriculture.multiblock;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumPilePosition implements IStringSerializable {
	INTERIOR,
	FRONT,
	BACK,
	SIDE_RIGHT,
	SIDE_LEFT,
	CORNER_FRONT_LEFT,
	CORNER_FRONT_RIGHT,
	CORNER_BACK_LEFT,
	CORNER_BACK_RIGHT;

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
