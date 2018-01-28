package forestry.book.pages;

import java.util.Collection;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import forestry.api.gui.IElementLayout;

public class BookPageElements extends BookPage {
	private IElementLayout layout;

	public BookPageElements(IElementLayout layout) {
		this.layout = layout;
	}

	@Override
	public void draw(GuiScreen gui, int startX, int startY, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(startX, startY, 0.0F);
		layout.draw(mouseX, mouseY);
		GlStateManager.popMatrix();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		layout.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public Collection<String> getTooltip(int mouseX, int mouseY) {
		if (layout.isMouseOver(mouseX, mouseY)) {
			return layout.getTooltip(mouseX, mouseY);
		}
		return super.getTooltip(mouseX, mouseY);
	}
}
