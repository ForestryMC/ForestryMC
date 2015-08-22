/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;

/** @deprecated since Forestry 3.4. Use ICrateRegistry */
@Deprecated
public interface IGenericCrate {

	void setContained(ItemStack crate, ItemStack contained);

	ItemStack getContained(ItemStack crate);

}
