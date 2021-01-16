package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.inventory.container.Slot;

public interface IContainerAnalyzerProvider extends IGuiSelectable {
	@Nullable
	Slot getAnalyzerSlot();
}
