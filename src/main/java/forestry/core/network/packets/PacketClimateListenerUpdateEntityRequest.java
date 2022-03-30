package forestry.core.network.packets;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

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
		public void onPacketData(PacketBufferForestry data, ServerPlayer player) {
			Entity entity = data.readEntityById(player.level);
			if (entity != null) {
				entity.getCapability(ClimateCapabilities.CLIMATE_LISTENER).ifPresent(l -> l.syncToClient(player));
			}
		}
	}
}
