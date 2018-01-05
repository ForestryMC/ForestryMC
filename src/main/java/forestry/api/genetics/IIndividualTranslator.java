/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * Translates {@link net.minecraft.block.state.IBlockState}s and {@link net.minecraft.item.ItemStack}s into genetic data.
 *
 * @deprecated Use a {@link IBlockTranslator} or a {@link IItemTranslator} to translate block states and item stacks.
 */
@Deprecated
public interface IIndividualTranslator<I extends IIndividual, O> {
	@Nullable
	I getIndividualFromObject(O objectToTranslate);

	default ItemStack getGeneticEquivalent(O objectToTranslate){
		return ItemStack.EMPTY;
	}
}
