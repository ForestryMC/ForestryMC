package forestry.sorting.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.IFilterLogic;
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

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();

			IFilterLogic logic = TileUtil.getInterface(player.world, pos, GeneticCapabilities.FILTER_LOGIC, null);
			if (logic != null) {
				logic.readGuiData(data);
			}
		}
	}
}
