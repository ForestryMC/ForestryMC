package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.climate.IClimateState;
import forestry.core.ClimateHandlerClient;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketClimatePlayer extends ForestryPacket implements IForestryPacketClient {

	private IClimateState climateState;

	public PacketClimatePlayer(IClimateState climateState) {
		this.climateState = climateState;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeClimateState(climateState);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CLIMATE_PLAYER;
	}

	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			ClimateHandlerClient.setCurrentState(data.readClimateState());
		}
	}
}
