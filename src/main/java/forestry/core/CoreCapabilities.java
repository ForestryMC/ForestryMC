package forestry.core;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import forestry.api.genetics.IIndividualHandler;

public class CoreCapabilities {
	/**
	 * Capability for {@link IIndividualHandler}.
	 */
	@CapabilityInject(IIndividualHandler.class)
	public static Capability<IIndividualHandler> INDIVIDUAL_HANDLER;
}
