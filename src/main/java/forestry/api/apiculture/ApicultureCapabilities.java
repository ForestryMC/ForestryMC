package forestry.api.apiculture;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ApicultureCapabilities {
	/**
	 * Capability for {@link IArmorApiarist}.
	 * The {@link Capability#getDefaultInstance()} will always protect the wearer with no side effects.
	 */
	@CapabilityInject(IArmorApiarist.class)
	public static Capability<IArmorApiarist> ARMOR_APIARIST;
}
