/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Locale;

public enum WoodBlockKind {
	LOG, PLANKS, SLAB, FENCE, FENCE_GATE, STAIRS, DOOR;

	public String getName() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return getName();
	}
}
