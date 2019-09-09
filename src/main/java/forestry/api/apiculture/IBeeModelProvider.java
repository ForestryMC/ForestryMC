/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.core.IModelManager;

public interface IBeeModelProvider {
	@OnlyIn(Dist.CLIENT)
	void registerModels(Item item, IModelManager manager);

	@OnlyIn(Dist.CLIENT)
	ModelResourceLocation getModel(EnumBeeType type);
}
