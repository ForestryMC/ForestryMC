package forestry.greenhouse.gui;

import javax.annotation.Nullable;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetCamouflageSlot;
import forestry.core.gui.widgets.WidgetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class WidgetCamouflageTab extends Widget {
	private final WidgetCamouflageSlot greenhouseSlot;
	@Nullable
	private final WidgetCamouflageSlot handlerSlot;
	private final ItemStack typeStack;

	public WidgetCamouflageTab(WidgetManager manager, int xPos, int yPos, IGreenhouseController controller, ICamouflageHandler camouflageHandler, String type) {
		super(manager, xPos, yPos);

		this.width = 48;
		this.height = 25;
		greenhouseSlot = new WidgetCamouflageSlot(manager, xPos + 26, yPos + 6, controller, type);
		if (camouflageHandler.canHandleType(type)) {
			handlerSlot = new WidgetCamouflageSlot(manager, xPos + 46, yPos + 6, camouflageHandler, type);
			width += 20;
		} else {
			handlerSlot = null;
		}
		switch (type) {
			case CamouflageManager.BLOCK:
				typeStack = new ItemStack(Blocks.BRICK_BLOCK);
				break;
			case CamouflageManager.GLASS:
				typeStack = new ItemStack(Blocks.GLASS);
				break;
			case CamouflageManager.DOOR:
				typeStack = new ItemStack(Items.OAK_DOOR);
				break;
			default:
				typeStack = ItemStack.EMPTY;
				break;
		}
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft minecraft = Minecraft.getMinecraft();
		TextureManager textureManager = minecraft.getTextureManager();
		textureManager.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 196, 0, 48, 25);
		if (handlerSlot != null) {
			manager.gui.drawTexturedModalRect(startX + xPos + 44, startY + yPos, 196, 25, 24, 25);
		}
		if (!typeStack.isEmpty()) {
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderItem renderItem = minecraft.getRenderItem();
			renderItem.renderItemIntoGUI(typeStack, startX + xPos + 6, startY + yPos + 6);
		}
		greenhouseSlot.draw(startX, startY);
		if (handlerSlot != null) {
			handlerSlot.draw(startX, startY);
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		mouseX -= manager.gui.getGuiLeft();
		mouseY -= manager.gui.getGuiTop();
		if (greenhouseSlot.isMouseOver(mouseX, mouseY)) {
			greenhouseSlot.handleMouseClick(mouseX, mouseY, mouseButton);
		} else if (handlerSlot != null && handlerSlot.isMouseOver(mouseX, mouseY)) {
			handlerSlot.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		return isMouseOver(mouseX - manager.gui.getGuiLeft(), mouseY - manager.gui.getGuiTop());
	}

	@Nullable
	public WidgetCamouflageSlot getHandlerSlot() {
		return handlerSlot;
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		if (greenhouseSlot.isMouseOver(mouseX, mouseY)) {
			return greenhouseSlot.getToolTip(mouseX, mouseY);
		} else if (handlerSlot != null && handlerSlot.isMouseOver(mouseX, mouseY)) {
			return handlerSlot.getToolTip(mouseX, mouseY);
		} else {
			return null;
		}
	}
}
