package forestry.core.gui.elements;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import forestry.api.gui.IGuiElement;
import forestry.core.gui.IGuiSizable;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.layouts.PaneLayout;

public class ElementManager<G extends Screen & IGuiSizable> {
	/* Attributes - Final */
	private final ElementGroup container;

	public ElementManager(G gui) {
		this.container = new PaneLayout(0, 0);
	}

	public ElementGroup group() {
		return container;
	}

	public void add(IGuiElement element) {
		container.add(element);
	}

	public void remove(IGuiElement element) {
		container.remove(element);
	}

	public void clear() {
		container.clear();
	}

	public void draw(int mouseX, int mouseY) {
		container.draw(mouseX, mouseY);
	}

	public void init(int guiLeft, int guiTop) {
		container.setLocation(guiLeft, guiTop);
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
	}

	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
	}

	public boolean keyTyped(char typedChar, int keyCode) {
		return false;
	}

	public void drawTooltip(int mouseX, int mouseY) {
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		int mX = mouseX - container.getX();
		int mY = mouseY - container.getY();
		return getElements().stream().anyMatch(element -> element.isMouseOver(mX, mY));
	}

	public List<ITextComponent> getTooltip(int mouseX, int mouseY) {
		return container.getTooltip(mouseX, mouseY);
	}

	public List<IGuiElement> getElements() {
		return container.getElements();
	}
}
