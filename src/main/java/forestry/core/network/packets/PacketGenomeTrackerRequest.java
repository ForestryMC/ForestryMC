package forestry.core.network.packets;

import java.io.IOException;
import java.util.Collection;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketGenomeTrackerRequest extends ForestryPacket implements IForestryPacketServer {

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.GENOME_TRACKER_REQUEST;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;
		Collection<ISpeciesRoot> speciesRoots = alleleRegistry.getSpeciesRoot().values();
		for (ISpeciesRoot speciesRoot : speciesRoots) {
			IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.getEntityWorld(), player.getGameProfile());
			breedingTracker.synchToPlayer(player);
		}
	}
}
