package forestry.api.arboriculture;

import java.util.Locale;

public enum WoodBlockKind {
	LOG, PLANKS, SLAB, FENCE, FENCE_GATE, STAIRS, DOOR;

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}
}
