package forestry.core.network.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;

import forestry.api.climate.ClimateCapabilities;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;

public class PacketClimateListenerUpdateEntityRequest extends ForestryPacket implements IForestryPacketServer {
	private final Entity entity;

	public PacketClimateListenerUpdateEntityRequest(Entity entity) {
		this.entity = entity;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeEntityById(entity);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CLIMATE_LISTENER_UPDATE_REQUEST_ENTITY;
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) {
			Entity entity = data.readEntityById(player.world);
			if (entity != null) {
				entity.getCapability(ClimateCapabilities.CLIMATE_LISTENER).ifPresent(l -> l.syncToClient(player));
			}
		}
	}
}
