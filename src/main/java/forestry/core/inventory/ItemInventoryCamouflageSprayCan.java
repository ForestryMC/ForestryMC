package forestry.core.inventory;

import forestry.api.core.ICamouflageHandler;
import forestry.core.network.packets.CamouflageSelectionType;
import forestry.core.network.packets.PacketCamouflageSelectServer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemInventoryCamouflageSprayCan extends ItemInventory implements ICamouflageHandler {

	public ItemInventoryCamouflageSprayCan(EntityPlayer player, ItemStack parent) {
		super(player, 1, parent);
	}

	@Override
	public ItemStack getCamouflageBlock(String type) {
		return getStackInSlot(0);
	}

	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
		return null;
	}

	@Override
	public boolean canHandleType(String type) {
		return true;
	}

	@Override
	public void setCamouflageBlock(String type, ItemStack camouflageBlock) {
		if(!ItemStackUtil.isIdenticalItem(camouflageBlock, getStackInSlot(0))){
			setInventorySlotContents(0, camouflageBlock);
			
			World world = player.worldObj;
			if (world != null && world.isRemote) {
				Proxies.net.sendToServer(new PacketCamouflageSelectServer(this, type, CamouflageSelectionType.ITEM));
			}
		}
	}

	@Override
	public BlockPos getCoordinates() {
		return new BlockPos(player);
	}

	@Override
	public World getWorldObj() {
		return null;
	}
}
