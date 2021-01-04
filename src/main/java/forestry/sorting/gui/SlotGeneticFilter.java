package forestry.sorting.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class SlotGeneticFilter extends Slot {
	private boolean enabled = true;

	public SlotGeneticFilter(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
