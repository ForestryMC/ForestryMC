/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class GeneticCapabilities {
	/**
	 * Capability for {@link IFilterLogic}.
	 */
	@CapabilityInject(IFilterLogic.class)
	public static Capability<IFilterLogic> FILTER_LOGIC;
}
