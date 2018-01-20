package forestry.sorting.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.sorting.network.packets.PacketFilterChangeGenome;
import forestry.sorting.network.packets.PacketFilterChangeRule;
import forestry.sorting.network.packets.PacketGuiFilterUpdate;

public class PacketRegistrySorting implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.FILTER_CHANGE_RULE.setPacketHandler(new PacketFilterChangeRule.Handler());
		PacketIdServer.FILTER_CHANGE_GENOME.setPacketHandler(new PacketFilterChangeGenome.Handler());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerPacketsClient() {
		PacketIdClient.GUI_UPDATE_FILTER.setPacketHandler(new PacketGuiFilterUpdate.Handler());
	}
}
