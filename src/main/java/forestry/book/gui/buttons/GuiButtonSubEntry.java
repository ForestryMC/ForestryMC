package forestry.book.gui.buttons;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;

import forestry.api.book.IBookEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

public class GuiButtonSubEntry extends GuiButton implements IToolTipProvider {
	public final IBookEntry selectedEntry;
	public final IBookEntry subEntry;

	public GuiButtonSubEntry(int buttonId, int x, int y, IBookEntry subEntry, IBookEntry selectedEntry) {
		super(buttonId, x, y, 24, 21, "");
		this.subEntry = subEntry;
		this.selectedEntry = selectedEntry;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		boolean active = subEntry == selectedEntry;
		TextureManager manager = mc.renderEngine;
		manager.bindTexture(GuiForesterBook.TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y, 48 + (active ? 24 : 0), 201, 24, 21);
		GlStateManager.translate(x + 8.0F, y + 4.0F, zLevel);
		GlStateManager.scale(0.85F, 0.85F, 0.85F);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		GuiUtil.drawItemStack(mc.fontRenderer, subEntry.getStack(), 0, 0);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
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
