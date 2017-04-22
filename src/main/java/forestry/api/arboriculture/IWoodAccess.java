/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * Provides easy access to Forestry and Vanilla wood items.
 * Forestry wood blocks have the same block state properties as vanilla ones.
 * Note that all doors are fireproof (even vanilla).
 *
 * @see WoodBlockKind
 * @see EnumForestryWoodType
 * @see EnumVanillaWoodType
 */
public interface IWoodAccess {

	ItemStack getStack(IWoodType woodType, WoodBlockKind kind, boolean fireproof);

	IBlockState getBlock(IWoodType woodType, WoodBlockKind kind, boolean fireproof);

	List<IWoodType> getRegisteredWoodTypes();

	void register(IWoodType woodType, WoodBlockKind woodBlockKind, boolean fireproof, IBlockState blockState, ItemStack itemStack);
}
