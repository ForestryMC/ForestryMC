/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

//import net.minecraft.client.renderer.ItemMeshDefinition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO flatten?
@OnlyIn(Dist.CLIENT)
public interface IWoodItemMeshDefinition {//extends ItemMeshDefinition {

	ResourceLocation getDefaultModelLocation(ItemStack stack);

}
