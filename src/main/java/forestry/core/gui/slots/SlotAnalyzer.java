package forestry.core.gui.slots;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.inventory.ItemInventoryAlyzer;

public class SlotAnalyzer extends SlotFiltered {
	@Nullable
	@OnlyIn(Dist.CLIENT)
	private Supplier<Boolean> visibleSupplier;

	public SlotAnalyzer(ItemInventoryAlyzer inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}

	public void setVisibleCallback(@Nullable Supplier<Boolean> visibleSupplier) {
		this.visibleSupplier = visibleSupplier;
	}

	@Override
	public boolean isActive() {
		return visibleSupplier != null && visibleSupplier.get();
	}
}
