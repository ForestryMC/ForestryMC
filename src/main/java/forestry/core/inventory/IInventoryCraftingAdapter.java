package forestry.core.inventory;

import net.minecraft.inventory.ISidedInventory;

import forestry.api.core.INBTTagable;
import forestry.core.interfaces.IFilterSlotDelegate;

public interface IInventoryCraftingAdapter extends ISidedInventory, IFilterSlotDelegate, INBTTagable {

	/* Sided */
	public IInventoryCraftingAdapter configureSided(int[] sides, int[] slots);
}
