package forestry.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.core.config.ForestryItem;

public abstract class AlyzerInventory extends ItemInventory {

	public static final int SLOT_SPECIMEN = 0;
	public static final int SLOT_ANALYZE_1 = 1;
	public static final int SLOT_ANALYZE_2 = 2;
	public static final int SLOT_ANALYZE_3 = 3;
	public static final int SLOT_ANALYZE_4 = 4;
	public static final int SLOT_ANALYZE_5 = 6;
	public static final int SLOT_ENERGY = 5;

	protected EntityPlayer player;

	public AlyzerInventory(Class<? extends Item> itemClass, int size, ItemStack itemstack) {
		super(itemClass, size, itemstack);
	}

	protected boolean isEnergy(ItemStack itemstack) {
		if (itemstack == null || itemstack.stackSize <= 0)
			return false;

		return ForestryItem.honeyDrop.isItemEqual(itemstack) || ForestryItem.honeydew.isItemEqual(itemstack);
	}

	protected boolean hasSpecimen() {
		for (int i = SLOT_SPECIMEN; i <= SLOT_ANALYZE_5; i++) {
			if (i == SLOT_ENERGY)
				continue;

			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null)
				return true;
		}
		return false;
	}

	protected abstract boolean isSpecimen(ItemStack itemStack);

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_ENERGY) {
			return isEnergy(itemStack);
		} else {
			return !hasSpecimen() && isSpecimen(itemStack);
		}
	}

}
