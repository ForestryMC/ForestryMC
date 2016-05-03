/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public interface IInternalBlockFace {
	
	boolean isTested();
	
	void setTested(boolean tested);
	
	@Nonnull
	EnumFacing getFace();
	
	@Nonnull
	BlockPos getPos();

}
