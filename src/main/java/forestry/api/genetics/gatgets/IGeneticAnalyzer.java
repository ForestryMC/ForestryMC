package forestry.api.genetics.gatgets;

import forestry.core.gui.elements.lib.IGuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
    void drawTooltip(Screen gui, int mouseX, int mouseY);

    /**
     * @return IGuiElement item element
     */
    IGuiElement getItemElement();

    void updateSelected();

    void setSelectedSlot(int selectedSlot);

    int getSelected();
}
