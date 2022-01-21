package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.world.inventory.Slot;

public interface IContainerAnalyzerProvider extends IGuiSelectable {
	@Nullable
	Slot getAnalyzerSlot();
}
