package forestry.cultivation.gui.widgets;

import javax.annotation.Nullable;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.api.farming.FarmDirection;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import forestry.cultivation.inventory.InventoryPlanter;

public class GhostItemStackWidget extends ItemStackWidget {
	private final Slot slot;

	public GhostItemStackWidget(WidgetManager widgetManager, int xPos, int yPos, ItemStack itemStack, Slot slot) {
		super(widgetManager, xPos, yPos, itemStack);
		this.slot = slot;
	}

	@Override
	public void draw(int startX, int startY) {
		if (!slot.getHasStack()) {
			super.draw(startX, startY);
		}
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();

		String directionString = getDirectionString();
		if (!directionString.isEmpty()) {
			FontRenderer fontRenderer = manager.minecraft.fontRenderer;
			fontRenderer.drawStringWithShadow(getDirectionString(), xPos + startX + 5, yPos + startY + 4, ColourProperties.INSTANCE.get("gui.screen"));
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(xPos + startX, yPos + startY, 206, 0, 16, 16);

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
	}

	private String getDirectionString() {
		if (slot.getSlotIndex() >= InventoryPlanter.SLOT_PRODUCTION_1 || slot.getSlotIndex() < InventoryPlanter.SLOT_RESOURCES_1 + InventoryPlanter.SLOT_RESOURCES_COUNT) {
			return "";
		}
		int index = slot.getSlotIndex() % 4;
		FarmDirection direction = FarmDirection.values()[index];
		String directionString = direction.toString().toLowerCase(Locale.ENGLISH);
		return Translator.translateToLocal("for.gui.planter." + directionString);
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return null;
	}
}
