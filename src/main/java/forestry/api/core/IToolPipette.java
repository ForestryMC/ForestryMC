/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.item.ItemStack;

/**
 * Taken from BuildCraft 5.0.x
 */
public interface IToolPipette {
	/**
	 * @return true if the pipette can pipette.
	 */
	boolean canPipette(ItemStack pipette);
}
