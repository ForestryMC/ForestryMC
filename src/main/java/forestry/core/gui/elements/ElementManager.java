package forestry.core.gui.elements;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraftforge.fml.client.config.GuiUtils;

import forestry.api.gui.IGuiElement;
import forestry.core.gui.IGuiSizable;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.layouts.PaneLayout;

public class ElementManager<G extends GuiScreen & IGuiSizable> {
	/* Attributes - Final */
	private final GuiState<G> state;
	private final ElementGroup container;

	public ElementManager(G gui) {
		this.state = new GuiState<>(gui);
		this.container = new PaneLayout(0, 0);
		this.container.setGuiState(state);
	}

	public ElementGroup group(){
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
		state.setMouseX(mouseX);
		state.setMouseY(mouseY);
		container.draw(mouseX, mouseY);
	}

	public void init(int guiLeft, int guiTop) {
		container.setLocation(guiLeft, guiTop);
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		container.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
		container.mouseClickMove(mouseX, mouseY, mouseButton);
	}

	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		container.mouseReleased(mouseX, mouseY, mouseButton);
	}

	public boolean keyTyped(char typedChar, int keyCode) {
		return container.keyTyped(typedChar, keyCode);
	}

	public void drawTooltip(int mouseX, int mouseY) {
		List<String> lines = getTooltip(mouseX, mouseY);
		if (!lines.isEmpty()) {
			GlStateManager.pushMatrix();
			ScaledResolution scaledresolution = new ScaledResolution(state.getMinecraft());
			GuiUtils.drawHoveringText(lines, mouseX, mouseY, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -1, state.getFontRenderer());
			GlStateManager.popMatrix();
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		int mX = mouseX - container.getX();
		int mY = mouseY - container.getY();
		return getElements().stream().anyMatch(element -> element.isMouseOver(mX, mY));
	}

	public List<String> getTooltip(int mouseX, int mouseY) {
		return container.getTooltip(mouseX, mouseY);
	}

	public List<IGuiElement> getElements() {
		return container.getElements();
	}
}
