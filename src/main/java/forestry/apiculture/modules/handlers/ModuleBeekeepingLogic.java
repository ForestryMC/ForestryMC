package forestry.apiculture.modules.handlers;

import forestry.api.apiculture.IBeeHousing;
import forestry.apiculture.BeekeepingLogic;
import forestry.apiculture.network.packets.PacketBeeLogicActiveModule;
import forestry.core.proxy.Proxies;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class ModuleBeekeepingLogic extends BeekeepingLogic {

	private IBeeHousing housing;
	
	public ModuleBeekeepingLogic(IBeeHousing housing) {
		super(housing);
		this.housing = housing;
	}
	
	@Override
	public void syncToClient() {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			Proxies.net.sendNetworkPacket(new PacketBeeLogicActiveModule(housing), world);
		}
	}
	
	@Override
	public void syncToClient(EntityPlayerMP player) {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			Proxies.net.sendToPlayer(new PacketBeeLogicActiveModule(housing), player);
		}
	}

}
