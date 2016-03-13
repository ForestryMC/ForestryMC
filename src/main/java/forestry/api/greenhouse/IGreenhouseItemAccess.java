/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nonnull;

import forestry.api.core.EnumCamouflageType;
import net.minecraft.item.ItemStack;

public interface IGreenhouseItemAccess {

	void registerGreenhouseGlass(@Nonnull ItemStack glass);
	
	boolean isGreenhouseGlass(@Nonnull ItemStack glass);
	
	void addToCamouflageBlockBlackList(@Nonnull EnumCamouflageType type, @Nonnull ItemStack camouflageBlock);
	
	boolean isOnCamouflageBlockBlackList(@Nonnull EnumCamouflageType type, @Nonnull ItemStack camouflageBlock);
	
}
