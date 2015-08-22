/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.Map;

import forestry.api.genetics.IAlleleSpecies;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	 * @return Map of possible products with the chance for drop each bee cycle. (0 - 100)
	 */
	Map<ItemStack, Integer> getProducts();

	/**
	 * @return Map of possible specialities with the chance for drop each bee cycle. (0 - 100)
	 */
	Map<ItemStack, Integer> getSpecialty();

	/**
	 * Only jubilant bees produce specialities.
	 * @return true if the bee is jubilant, false otherwise.
	 */
	boolean isJubilant(IBeeGenome genome, IBeeHousing housing);

	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getIcon(EnumBeeType type, int renderPass);

	/**
	 * @return Path of the texture to use for entity rendering.
	 */
	String getEntityTexture();
}
