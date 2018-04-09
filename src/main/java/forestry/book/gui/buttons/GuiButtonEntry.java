package forestry.book.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.IBookEntry;
import forestry.core.gui.GuiUtil;

@SideOnly(Side.CLIENT)
public class GuiButtonEntry extends GuiButton {
	public final IBookEntry entry;

	public GuiButtonEntry(int buttonId, int x, int y, IBookEntry entry) {
		super(buttonId, x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(entry.getTitle()) + 9, 11, entry.getTitle());
		this.entry = entry;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			FontRenderer fontRenderer = mc.fontRenderer;
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			String text = displayString;
			if (hovered) {
				text = TextFormatting.GOLD + text;
			} else {
				text = TextFormatting.DARK_GRAY + text;
			}

			boolean unicode = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(true);
			fontRenderer.drawString(text, this.x + 9, this.y, 0);
			fontRenderer.setUnicodeFlag(unicode);

			ItemStack stack = entry.getStack();
			if (!stack.isEmpty()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, zLevel);
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.enableRescaleNormal();
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GuiUtil.drawItemStack(fontRenderer, stack, 0, 0);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popMatrix();
			}
		}
	}
}
