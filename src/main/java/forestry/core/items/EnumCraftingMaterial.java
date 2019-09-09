package forestry.core.items;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumCraftingMaterial implements IStringSerializable {
	PULSATING_DUST,
	PULSATING_MESH,
	SILK_WISP,
	WOVEN_SILK,
	DISSIPATION_CHARGE,
	ICE_SHARD,
	SCENTED_PANELING,
	CAMOUFLAGED_PANELING;

	public static final EnumCraftingMaterial[] VALUES = values();

	private final String name;

	EnumCraftingMaterial() {
		this.name = toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String getName() {
		return name;
	}
}
