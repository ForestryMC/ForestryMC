package forestry.sorting.network.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRuleType;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.TileUtil;

public class PacketFilterChangeRule extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final EnumFacing facing;
	private final IFilterRuleType rule;

	public PacketFilterChangeRule(BlockPos pos, EnumFacing facing, IFilterRuleType rule) {
		this.pos = pos;
		this.facing = facing;
		this.rule = rule;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
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
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) {
			BlockPos pos = data.readBlockPos();
			EnumFacing facing = EnumFacing.byIndex(data.readShort());
			IFilterRuleType rule = AlleleManager.filterRegistry.getRuleOrDefault(data.readShort());
			IFilterLogic logic = TileUtil.getInterface(player.world, pos, GeneticCapabilities.FILTER_LOGIC, null);
			if (logic != null) {
				if (logic.setRule(facing, rule)) {
					logic.getNetworkHandler().sendToPlayers(logic, player.getServerWorld(), player);
				}
			}
		}
	}
}
