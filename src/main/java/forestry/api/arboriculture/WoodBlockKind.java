/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum WoodBlockKind implements StringRepresentable {
	LOG, PLANKS, SLAB, FENCE, FENCE_GATE, STAIRS, DOOR;

	public String getSerializedName() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return getSerializedName();
	}
}
