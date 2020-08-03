package forestry.core.gui;

import net.minecraft.inventory.container.Slot;

import javax.annotation.Nullable;

public interface IContainerAnalyzerProvider extends IGuiSelectable {
    @Nullable
    Slot getAnalyzerSlot();
}
