package forestry.apiculture.blocks;

import java.util.Locale;

import forestry.api.core.IBlockSubtype;
import forestry.apiculture.features.ApicultureTiles;
import forestry.modules.features.FeatureTileType;

public enum BlockAlvearyType implements IBlockSubtype {
	PLAIN(false),
	SWARMER(true),
	FAN(true),
	HEATER(true),
	HYGRO(false),
	STABILISER(false),
	SIEVE(false);

	public static final BlockAlvearyType[] VALUES = values();

	public final boolean activatable;

	BlockAlvearyType(boolean activatable) {
		this.activatable = activatable;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String getSerializedName() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	public FeatureTileType<?> getTileType() {
		return switch (this) {
			case PLAIN -> ApicultureTiles.ALVEARY_PLAIN;
			case SWARMER -> ApicultureTiles.ALVEARY_SWARMER;
			case FAN -> ApicultureTiles.ALVEARY_FAN;
			case HEATER -> ApicultureTiles.ALVEARY_HEATER;
			case HYGRO -> ApicultureTiles.ALVEARY_HYGROREGULATOR;
			case STABILISER -> ApicultureTiles.ALVEARY_STABILISER;
			case SIEVE -> ApicultureTiles.ALVEARY_SIEVE;
		};
	}
}
