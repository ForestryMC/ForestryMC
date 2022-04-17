/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.genetics.IAlleleSpecies;

public interface IAlleleBeeSpecies extends IAlleleSpecies {

	/**
	 * @return the IBeeRoot
	 */
	IBeeRoot getRoot();

	/**
	 * @return true if this species is only active at night.
	 */
	boolean isNocturnal();

	/**
	 * @return Map of possible products with the chance for drop each bee cycle. (0 - 1]
	 */
	Map<ItemStack, Float> getProductChances();

	/**
	 * @return Map of possible specialities with the chance for drop each bee cycle. (0 - 1]
	 */
	Map<ItemStack, Float> getSpecialtyChances();

	/**
	 * Only jubilant bees produce specialities.
	 * @return true if the bee is jubilant, false otherwise.
	 */
	boolean isJubilant(IBeeGenome genome, IBeeHousing housing);

	@SideOnly(Side.CLIENT)
	IIcon getIcon(EnumBeeType type, int renderPass);

	/**
	 * @deprecated since Forestry 3.6.
	 * @return Path of the texture to use for entity rendering.
	 */
	@Deprecated
	String getEntityTexture();

}
