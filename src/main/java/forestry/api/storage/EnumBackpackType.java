/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import java.util.Locale;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;

import forestry.core.config.Constants;
import forestry.storage.BackpackMode;

public enum EnumBackpackType implements IStringSerializable {
	NORMAL, WOVEN, NATURALIST;

	@Override
	public String getString() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public ModelResourceLocation getLocation(BackpackMode mode) {
		String typeName = getString();
		if (this == NATURALIST) {
			typeName = NORMAL.getString();
		}
		return new ModelResourceLocation(Constants.MOD_ID + ":backpacks/" + typeName + "_" + mode.getString(), "inventory");
	}
}
