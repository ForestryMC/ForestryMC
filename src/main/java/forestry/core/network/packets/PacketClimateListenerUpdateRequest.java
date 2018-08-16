package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.IClimateListener;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;

public class PacketClimateListenerUpdateRequest extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;

	public PacketClimateListenerUpdateRequest(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CLIMATE_LISTENER_UPDATE_REQUEST;
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			TileEntity tileEntity = player.world.getTileEntity(pos);
			if (tileEntity != null && tileEntity.hasCapability(ClimateCapabilities.CLIMATE_LISTENER, null)) {
				IClimateListener listener = tileEntity.getCapability(ClimateCapabilities.CLIMATE_LISTENER, null);
				if (listener != null) {
					listener.syncToClient(player);
				}
			}
		}
	}
}
