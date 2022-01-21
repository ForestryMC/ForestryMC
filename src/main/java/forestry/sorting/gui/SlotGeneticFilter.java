package forestry.sorting.gui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotGeneticFilter extends Slot {
	private boolean enabled = true;

	public SlotGeneticFilter(Container inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isActive() {
		return enabled;
	}
}
