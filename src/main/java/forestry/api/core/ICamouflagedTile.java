/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.util.math.BlockPos;

public interface ICamouflagedTile {

	String getCamouflageType();

	BlockPos getCoordinates();

}
