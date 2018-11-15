package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateState;
import forestry.core.climate.ClimateRoot;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketClimateListenerUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final IClimateState state;

	public PacketClimateListenerUpdate(BlockPos pos, IClimateState state) {
		this.pos = pos;
		this.state = state;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeClimateState(state);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CLIMATE_LISTENER_UPDATE;
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			IClimateState state = data.readClimateState();
			IClimateListener listener = ClimateRoot.getInstance().getListener(player.world, pos);
			if (listener != null) {
				listener.setClimateState(state);
			}
		}
	}
}
