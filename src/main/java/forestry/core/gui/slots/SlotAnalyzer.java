package forestry.core.gui.slots;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.GuiAnalyzerProvider;
import forestry.core.inventory.ItemInventoryAlyzer;

public class SlotAnalyzer extends SlotFiltered {
	@Nullable
	@OnlyIn(Dist.CLIENT)
	private GuiAnalyzerProvider gui;

	public SlotAnalyzer(ItemInventoryAlyzer inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}

	public void setPosition(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public void setGui(@Nullable GuiAnalyzerProvider gui) {
		this.gui = gui;
	}

	@Override
	public boolean isEnabled() {
		return gui != null && gui.analyzer.isVisible();
	}
}
