/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.util.math.Vec3i;

/**
 * Simple interface to allow adding additional alleles containing float values.
 */
public interface IAlleleArea extends IAllele {

	Vec3i getValue();

}
