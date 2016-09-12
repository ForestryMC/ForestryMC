package forestry.core.gui;

import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCamouflageSprayCan extends ContainerItemInventory<ItemInventoryCamouflageSprayCan> {

	public ContainerCamouflageSprayCan(ItemInventoryCamouflageSprayCan inventory, InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 8, 84);
	}

}
