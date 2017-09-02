/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.circuits.ICircuit;
import forestry.api.core.ILocatable;

public interface IClimateSourceOwner extends ILocatable {
	IClimateSource getClimateSource();

	@SideOnly(Side.CLIENT)
	ItemStack getItemStack();

	boolean isActive();

	/**
	 * @return true if this climate source owner can hold a {@link ICircuit}
	 */
	boolean isCircuitable();
}
