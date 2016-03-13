/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import forestry.core.tiles.ILocatable;
import net.minecraft.item.ItemStack;

public interface ICamouflageHandler extends ILocatable {

    ItemStack getCamouflageBlock(EnumCamouflageType type);

    void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock);
	
}
