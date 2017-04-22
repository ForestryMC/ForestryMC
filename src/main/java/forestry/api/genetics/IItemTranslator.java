/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
/**
 * Translates items into genetic data. Used by the treealyzer and the farm to convert foreign saplings.
 */
public interface IItemTranslator<I extends IIndividual> extends IIndividualTranslator<I, ItemStack> {
	
	@Nullable
	@Override
	I getIndividualFromObject(ItemStack itemStack);
}
