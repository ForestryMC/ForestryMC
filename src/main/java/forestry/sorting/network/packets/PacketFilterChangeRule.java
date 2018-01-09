package forestry.sorting.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterRule;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.TileUtil;
import forestry.sorting.tiles.TileGeneticFilter;

public class PacketFilterChangeRule extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final EnumFacing facing;
	private final IFilterRule rule;

	public PacketFilterChangeRule(TileGeneticFilter filter, EnumFacing facing, IFilterRule rule) {
		this.pos = filter.getPos();
		this.facing = facing;
		this.rule = rule;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeShort(facing.getIndex());
		data.writeShort(AlleleManager.filterRegistry.getId(rule));
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.FILTER_CHANGE_RULE;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			EnumFacing facing = EnumFacing.getFront(data.readShort());
			IFilterRule rule = AlleleManager.filterRegistry.getRuleOrDefault(data.readShort());
			TileUtil.actOnTile(player.world, pos, TileGeneticFilter.class, tile -> {
				if (tile.setRule(facing, rule)) {
					tile.sendToPlayers(player.getServerWorld(), player);
				}
			});
		}
	}
}
