package forestry.apiculture.blocks;

import java.util.Locale;

public enum BlockAlvearyType {
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
}
