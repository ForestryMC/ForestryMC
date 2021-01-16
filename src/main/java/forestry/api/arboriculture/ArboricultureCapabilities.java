/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import forestry.api.core.IArmorNaturalist;

public class ArboricultureCapabilities {
	/**
	 * Capability for {@link IArmorNaturalist}.
	 * The {@link Capability#getDefaultInstance()} will always show pollination with no side effects.
	 */
	@CapabilityInject(IArmorNaturalist.class)
	public static Capability<IArmorNaturalist> ARMOR_NATURALIST;
}
