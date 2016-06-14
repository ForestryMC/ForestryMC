package forestry.factory.blocks;

import java.util.Locale;

public enum BlockDistillVatType {
	PLAIN(false);

	public static final BlockDistillVatType[] VALUES = values();

	public final boolean activatable;

	BlockDistillVatType(boolean activatable) {
		this.activatable = activatable;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}
}
