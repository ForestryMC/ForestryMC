/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import forestry.api.genetics.filter.IFilterLogic;

public class GeneticCapabilities {
	/**
	 * Capability for {@link IFilterLogic}.
	 */
	public static Capability<IFilterLogic> FILTER_LOGIC = CapabilityManager.get(new CapabilityToken<>() {});
}
