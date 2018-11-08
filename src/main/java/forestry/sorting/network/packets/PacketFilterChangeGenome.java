package forestry.sorting.network.packets;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFilterLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.TileUtil;

public class PacketFilterChangeGenome extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final EnumFacing facing;
	private final short index;
	private final boolean active;
	@Nullable
	private final IAllele allele;

	public PacketFilterChangeGenome(BlockPos pos, EnumFacing facing, short index, boolean active, @Nullable IAllele allele) {
		this.pos = pos;
		this.facing = facing;
		this.index = index;
		this.active = active;
		this.allele = allele;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeShort(facing.getIndex());
		data.writeShort(index);
		data.writeBoolean(active);
		if (allele != null) {
			data.writeBoolean(true);
			data.writeString(allele.getUID());
		} else {
			data.writeBoolean(false);
		}
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.FILTER_CHANGE_GENOME;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) {
			BlockPos pos = data.readBlockPos();
			EnumFacing facing = EnumFacing.byIndex(data.readShort());
			short index = data.readShort();
			boolean active = data.readBoolean();
			IAllele allele;
			if (data.readBoolean()) {
				allele = AlleleManager.alleleRegistry.getAllele(data.readString());
			} else {
				allele = null;
			}
			IFilterLogic logic = TileUtil.getInterface(player.world, pos, GeneticCapabilities.FILTER_LOGIC, null);
			if (logic != null) {
				if (logic.setGenomeFilter(facing, index, active, allele)) {
					logic.getNetworkHandler().sendToPlayers(logic, player.getServerWorld(), player);
				}
			}
		}
	}
}
