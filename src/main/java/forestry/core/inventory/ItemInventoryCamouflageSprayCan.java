package forestry.core.inventory;

import forestry.api.core.ICamouflageHandler;
import forestry.core.network.packets.CamouflageSelectionType;
import forestry.core.network.packets.PacketCamouflageSelectServer;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
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
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canHandleType(String type) {
		return true;
	}

	@Override
	public boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, getStackInSlot(0))) {
			setInventorySlotContents(0, camouflageBlock);

			World world = player.world;
			if (sendClientUpdate && world != null && world.isRemote) {
				NetworkUtil.sendToServer(new PacketCamouflageSelectServer(this, type, CamouflageSelectionType.ITEM));
			}
			return true;
		}
		return false;
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
