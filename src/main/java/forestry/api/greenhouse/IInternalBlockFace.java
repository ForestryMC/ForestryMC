/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IInternalBlockFace {

	boolean isTested();

	void setTested(boolean tested);

	EnumFacing getFace();

	BlockPos getPos();

}
