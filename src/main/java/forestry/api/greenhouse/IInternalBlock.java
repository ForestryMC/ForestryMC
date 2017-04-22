/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.util.math.BlockPos;

public interface IInternalBlock {

	BlockPos getPos();

	@Nullable
	IInternalBlock getRoot();

	Collection<IInternalBlockFace> getFaces();

}
