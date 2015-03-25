package forestry.arboriculture.network;

import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.network.PacketIds;

public class PacketSapling extends PacketTreeContainer {

	public PacketSapling() {

	}

	public PacketSapling(TileSapling sapling) {
		super(PacketIds.SAPLING, sapling);
	}

}
