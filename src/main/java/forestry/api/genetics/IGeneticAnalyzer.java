package forestry.api.genetics;

import net.minecraft.client.gui.GuiScreen;

import forestry.api.gui.IGuiElement;

public interface IGeneticAnalyzer extends IGuiElement {
	IGeneticAnalyzerProvider getProvider();

	/**
	 * @return True if the analyzer is currently visible.
	 */
	boolean isVisible();

	void setVisible(boolean visible);

	/**
	 * Called at the end of the constructor of the analyzer provider.
	 */
	void init();

	/**
	 * Updates the displayed content of the analyzer.
	 */
	void update();

	/**
	 * Draws the tooltip of the element that is under the mouse.
	 */
	void drawTooltip(GuiScreen gui, int mouseX, int mouseY);

	/**
	 *
	 * @return
	 */
	IGuiElement getItemElement();

	void updateSelected();

	void setSelectedSlot(int selectedSlot);

	int getSelected();
}
