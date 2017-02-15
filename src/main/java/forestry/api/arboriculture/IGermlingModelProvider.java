/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.core.IModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Provides icons for saplings, pollen, etc.
 * You must implement this yourself to use TreeManager.treeFactory.createSpecies()
 * There is no default implementation because every sapling has a unique icon.
 */
public interface IGermlingModelProvider {
	@SideOnly(Side.CLIENT)
	void registerModels(Item item, IModelManager manager, EnumGermlingType type);

	@SideOnly(Side.CLIENT)
	ModelResourceLocation getModel(EnumGermlingType type);

	/**
	 * Provides color for sapling. See {@link ILeafSpriteProvider} for pollen color.
	 *
	 * @param type       the germling type to render
	 * @param renderPass renderPass of rendering
	 * @return sapling color for renderPass
	 */
	int getSpriteColor(EnumGermlingType type, int renderPass);
}
