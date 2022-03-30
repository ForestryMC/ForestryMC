package forestry.sorting.network.packets;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;
import forestry.sorting.tiles.IFilterContainer;

public class PacketGuiFilterUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final IFilterLogic logic;

	public PacketGuiFilterUpdate(IFilterContainer container) {
		this.pos = container.getCoordinates();
		this.logic = container.getLogic();
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		logic.writeGuiData(data);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_UPDATE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, Player player) {
			BlockPos pos = data.readBlockPos();

			LazyOptional<IFilterLogic> logic = TileUtil.getInterface(player.level, pos, GeneticCapabilities.FILTER_LOGIC, null);
			logic.ifPresent(l -> l.readGuiData(data));
		}
	}
}
