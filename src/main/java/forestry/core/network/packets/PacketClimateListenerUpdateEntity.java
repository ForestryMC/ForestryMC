package forestry.core.network.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateState;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketClimateListenerUpdateEntity extends ForestryPacket implements IForestryPacketClient {
	private final Entity entity;
	private final IClimateState state;

	public PacketClimateListenerUpdateEntity(Entity entity, IClimateState state) {
		this.entity = entity;
		this.state = state;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeEntityById(entity);
		data.writeClimateState(state);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CLIMATE_LISTENER_UPDATE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
			Entity entity = data.readEntityById(player.world);
			IClimateState state = data.readClimateState();
			if (entity != null) {
				LazyOptional<IClimateListener> listener = entity.getCapability(ClimateCapabilities.CLIMATE_LISTENER);
				listener.ifPresent(l -> l.setClimateState(state));
			}
		}
	}
}
