/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;

/**
 * Translates blockStates into genetic data.
 * Used by bees and butterflies to convert and pollinate foreign leaf blocks.
 */
public interface IBlockTranslator<I extends IIndividual> extends IIndividualTranslator<I, IBlockState> {
	@Nullable
	@Override
	I getIndividualFromObject(IBlockState blockState);
}
