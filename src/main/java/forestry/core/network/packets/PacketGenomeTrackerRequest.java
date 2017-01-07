package forestry.core.network.packets;

import java.io.IOException;
import java.util.Collection;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketGenomeTrackerRequest extends ForestryPacket implements IForestryPacketServer {
	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.GENOME_TRACKER_REQUEST;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		// no data, just need to know which player is requesting information
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;
			Collection<ISpeciesRoot> speciesRoots = alleleRegistry.getSpeciesRoot().values();
			for (ISpeciesRoot speciesRoot : speciesRoots) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.getEntityWorld(), player.getGameProfile());
				breedingTracker.synchToPlayer(player);
			}
		}
	}
}
