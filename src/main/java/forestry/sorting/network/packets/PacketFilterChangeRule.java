package forestry.sorting.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.common.util.LazyOptional;

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
	private final Direction facing;
	private final IFilterRuleType rule;

	public PacketFilterChangeRule(BlockPos pos, Direction facing, IFilterRuleType rule) {
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
		public void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) {
			BlockPos pos = data.readBlockPos();
			Direction facing = Direction.byIndex(data.readShort());
			IFilterRuleType rule = AlleleManager.filterRegistry.getRuleOrDefault(data.readShort());
			LazyOptional<IFilterLogic> logic = TileUtil.getInterface(player.world, pos, GeneticCapabilities.FILTER_LOGIC, null);
			logic.ifPresent(l -> {
				if (l.setRule(facing, rule)) {
					l.getNetworkHandler().sendToPlayers(l, player.getServerWorld(), player);
				}
			});
		}
	}
}
