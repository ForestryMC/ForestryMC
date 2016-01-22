package forestry.farming.blocks;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumBlockFarmType implements IStringSerializable {
	BASIC,
	BAND,
	GEARBOX,
	HATCH,
	VALVE,
	CONTROL;

	public static final EnumBlockFarmType[] VALUES = values();
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
