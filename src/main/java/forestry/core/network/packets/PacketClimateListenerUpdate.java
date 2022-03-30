package forestry.core.network.packets;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

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

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, Player player) {
			BlockPos pos = data.readBlockPos();
			IClimateState state = data.readClimateState();
			LazyOptional<IClimateListener> listener = ClimateRoot.getInstance().getListener(player.level, pos);
			listener.ifPresent(l -> l.setClimateState(state));
		}
	}
}
