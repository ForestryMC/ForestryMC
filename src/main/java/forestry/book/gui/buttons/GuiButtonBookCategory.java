package forestry.book.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.IBookCategory;
import forestry.api.core.tooltips.IToolTipProvider;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.GuiUtil;

import genetics.Log;

@OnlyIn(Dist.CLIENT)
public class GuiButtonBookCategory extends Button implements IToolTipProvider {
	public final IBookCategory category;
	public final ItemStack stack;

	public GuiButtonBookCategory(int x, int y, IBookCategory category, IPressable action) {
		super(x, y, 32, 32, StringTextComponent.EMPTY, action);
		this.category = category;
		Log.info(String.format("Create Stack %s", category.getName()));
		this.stack = category.getStack();
		Log.info(String.format("Create category %s", category.getName()));
	}

	@Override
	public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			FontRenderer fontRenderer = Minecraft.getInstance().font;
			GlStateManager._pushMatrix();
			GlStateManager._translatef(x, y, getBlitOffset());    //TODO correct?
			GlStateManager._scalef(2F, 2F, 2F);
			GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
			//RenderHelper.enableGUIStandardItemLighting(); TODO: Gui Item Light
			GlStateManager._enableRescaleNormal();
			GuiUtil.drawItemStack(fontRenderer, stack, 0, 0);
			RenderHelper.turnOff();
			GlStateManager._popMatrix();
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		ToolTip toolTip = new ToolTip();
		toolTip.add(category.getLocalizedName());
		return toolTip;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

	@Override
	public boolean isHovering(double mouseX, double mouseY) {
		return isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean isRelativeToGui() {
		return false;
	}
}
