package forestry.core.gui.slots;

import net.minecraft.inventory.IInventory;

import forestry.core.tiles.IFilterSlotDelegate;

public class SlotLiquidIn extends SlotFiltered {
	public <T extends IInventory & IFilterSlotDelegate> SlotLiquidIn(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		setBackgroundTexture("slots/liquid");
	}
}
