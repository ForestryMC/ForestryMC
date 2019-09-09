/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.genetics.EnumGermlingType;

/**
 * Provides icons for saplings, pollen, etc.
 * You must implement this yourself to use TreeManager.treeFactory.createSpecies()
 * There is no default implementation because every sapling has a unique icon.
 */
public interface IGermlingModelProvider {
	@OnlyIn(Dist.CLIENT)
	ResourceLocation getItemModel();

	@OnlyIn(Dist.CLIENT)
	ResourceLocation getBlockModel();

	/**
	 * Provides color for sapling. See {@link ILeafSpriteProvider} for pollen color.
	 *
	 * @param type       the germling type to render
	 * @param renderPass renderPass of rendering
	 * @return sapling color for renderPass
	 */
	int getSpriteColor(EnumGermlingType type, int renderPass);
}
