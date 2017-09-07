package forestry.greenhouse.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.gui.tooltips.ToolTip;
import forestry.greenhouse.api.climate.IClimateModifier;

public class WidgetClimateModifier {
	public static final int WIDTH = 18;
	public static final int HEIGHT = 18;

	private final int xPos;
	private final int yPos;
	private final WidgetClimatePanel parent;
	private final IClimateModifier modifier;
	private final ToolTip toolTip = new ToolTip(250) {
		@Override
		@SideOnly(Side.CLIENT)
		public void refresh() {
			toolTip.clear();
			toolTip.add(modifier.getName());
			List<String> lines = new ArrayList<>();
			parent.gui.container.addModifierInformation(modifier, parent.getType(), lines);
			for (String line : lines) {
				toolTip.add(line, TextFormatting.GRAY);
			}
		}
	};

	public WidgetClimateModifier(WidgetClimatePanel parent, int xPos, int yPos, IClimateModifier modifier) {
		this.parent = parent;
		this.xPos = xPos;
		this.yPos = yPos;
		this.modifier = modifier;
	}

	public void draw(int startX, int startY) {
		if (!isVisible()) {
			return;
		}
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(modifier.getTextureMap());
		TextureAtlasSprite icon = modifier.getIcon();
		if (icon == null) {
			RenderHelper.enableGUIStandardItemLighting();
			ItemStack stack = modifier.getIconItemStack();
			FontRenderer font = null;
			if (!stack.isEmpty()) {
				font = stack.getItem().getFontRenderer(stack);
			}
			if (font == null) {
				font = parent.gui.getFontRenderer();
			}

			RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
			itemRender.renderItemAndEffectIntoGUI(null, stack, xPos + startX, yPos + startY);
			itemRender.renderItemOverlayIntoGUI(font, stack, xPos + startX, yPos + startY, null);
			RenderHelper.disableStandardItemLighting();
		} else {
			parent.gui.drawTexturedModalRect(startX + xPos, startY + yPos, icon, 16, 16);
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseX < xPos + WIDTH && mouseY >= yPos && mouseY < yPos + HEIGHT;
	}

	public ToolTip getToolTip(int mouseX, int mouseY) {
		if (!isVisible()) {
			return null;
		}
		if (!isMouseOver(mouseX, mouseY)) {
			return null;
		}
		return toolTip;
	}

	private boolean isVisible() {
		return true;
	}
}
