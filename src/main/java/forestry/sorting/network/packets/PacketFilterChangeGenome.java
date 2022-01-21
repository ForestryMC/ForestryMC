package forestry.sorting.network.packets;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.minecraftforge.common.util.LazyOptional;

import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.TileUtil;

import genetics.api.alleles.IAllele;
import genetics.utils.AlleleUtils;

public class PacketFilterChangeGenome extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final Direction facing;
	private final short index;
	private final boolean active;
	@Nullable
	private final IAllele allele;

	public PacketFilterChangeGenome(BlockPos pos, Direction facing, short index, boolean active, @Nullable IAllele allele) {
		this.pos = pos;
		this.facing = facing;
		this.index = index;
		this.active = active;
		this.allele = allele;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeShort(facing.get3DDataValue());
		data.writeShort(index);
		data.writeBoolean(active);
		if (allele != null) {
			data.writeBoolean(true);
			data.writeUtf(allele.getRegistryName().toString());
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
		public void onPacketData(PacketBufferForestry data, ServerPlayer player) {
			BlockPos pos = data.readBlockPos();
			Direction facing = Direction.from3DDataValue(data.readShort());
			short index = data.readShort();
			boolean active = data.readBoolean();
			IAllele allele;
			if (data.readBoolean()) {
				allele = AlleleUtils.getAlleleOrNull(data.readUtf());
			} else {
				allele = null;
			}
			LazyOptional<IFilterLogic> logic = TileUtil.getInterface(player.level, pos, GeneticCapabilities.FILTER_LOGIC, null);
			logic.ifPresent(l -> {
				if (l.setGenomeFilter(facing, index, active, allele)) {
					l.getNetworkHandler().sendToPlayers(l, player.getLevel(), player);
				}
			});
		}
	}
}
