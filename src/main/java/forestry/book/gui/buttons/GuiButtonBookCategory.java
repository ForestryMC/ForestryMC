package forestry.book.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

import forestry.api.book.IBookCategory;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

public class GuiButtonBookCategory extends GuiButton implements IToolTipProvider {
	public final IBookCategory category;

	public GuiButtonBookCategory(int buttonId, int x, int y, IBookCategory category) {
		super(buttonId, x, y, 32, 32, "");
		this.category = category;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(visible){
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			FontRenderer fontRenderer = mc.fontRenderer;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, zLevel);
			GlStateManager.scale(2F, 2F, 2F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			GuiUtil.drawItemStack(fontRenderer, category.getStack(), 0, 0);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}
		//super.drawButton(mc, mouseX, mouseY, partialTicks);
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		ToolTip toolTip = new ToolTip();
		toolTip.add(category.getLocalizedName());
		return toolTip;
	}

	@Override
	public boolean isToolTipVisible() {
		return true;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

	@Override
	public boolean isRelativeToGui() {
		return false;
	}
}
