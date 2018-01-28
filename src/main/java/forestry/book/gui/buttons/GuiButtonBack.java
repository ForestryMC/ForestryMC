package forestry.book.gui.buttons;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

public class GuiButtonBack extends GuiButton implements IToolTipProvider {
	public GuiButtonBack(int buttonId, int x, int y) {
		super(buttonId, x, y, 18, 9, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(visible){
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			TextureManager manager = mc.renderEngine;
			manager.bindTexture(GuiForesterBook.TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x, y, 36 + (hovered ? 18 : 0), 181, 18, 9);
		}
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return null;
	}

	@Override
	public boolean isToolTipVisible() {
		return false;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return false;
	}
}
