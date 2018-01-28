package forestry.api.book;

import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public interface IBookPage {

	void initPage(GuiScreen gui);

	void draw(GuiScreen gui, int startX, int startY, int mouseX, int mouseY);

	void onButtonPressed(GuiScreen gui, GuiButton button);

	void mouseClicked(int mouseX, int mouseY, int mouseButton);

	Collection<String> getTooltip(int mouseX, int mouseY);
}
