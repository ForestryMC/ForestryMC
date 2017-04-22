package forestry.core.gui.slots;

import forestry.core.tiles.IFilterSlotDelegate;
import net.minecraft.inventory.IInventory;

public class SlotLiquidIn extends SlotFiltered {
	public <T extends IInventory & IFilterSlotDelegate> SlotLiquidIn(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		setBackgroundTexture("slots/liquid");
	}
}
