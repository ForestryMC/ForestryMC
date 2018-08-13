package forestry.storage.inventory;

import com.google.common.base.Preconditions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.storage.IBackpackDefinition;
import forestry.core.inventory.ItemInventory;
import forestry.storage.items.ItemBackpack;

public class ItemInventoryBackpack extends ItemInventory {

	private final IBackpackDefinition backpackDefinition;

	public ItemInventoryBackpack(EntityPlayer player, int size, ItemStack parent) {
		super(player, size, parent);

		Item item = parent.getItem();
		Preconditions.checkArgument(item instanceof ItemBackpack, "Parent must be a backpack.");

		this.backpackDefinition = ((ItemBackpack) item).getDefinition();
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return backpackDefinition.getFilter().test(itemStack);
	}
}
