package forestry.storage.inventory;

import com.google.common.base.Preconditions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import forestry.api.storage.IBackpackDefinition;
import forestry.core.inventory.ItemInventory;
import forestry.storage.items.ItemBackpack;

public class ItemInventoryBackpack extends ItemInventory {

	private final IBackpackDefinition backpackDefinition;

	public ItemInventoryBackpack(Player player, int size, ItemStack parent) {
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
