package forestry.database.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.NetworkUtil;
import forestry.database.DatabaseItem;
import forestry.database.gui.GuiDatabase;
import forestry.database.network.packets.PacketExtractItem;
import forestry.database.network.packets.PacketInsertItem;

public class WidgetDatabaseSlot extends Widget {
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/database_inventory.png");

	public static final Drawable SLOT = new Drawable(TEXTURE_LOCATION, 218, 0, 22, 22);
	public static final Drawable SLOT_SELECTED = new Drawable(TEXTURE_LOCATION, 218, 22, 22, 22);

	private int xPos;
	private int yPos;
	private boolean isEmpty;
	private int databaseIndex;
	private boolean ignoreMouseUp = false;
	private boolean mouseOver;

	public WidgetDatabaseSlot(WidgetManager manager) {
		super(manager, 0, 0);
		this.databaseIndex = -1;
		this.isEmpty = false;
	}

	public void update(int xPos, int yPos, int databaseIndex, boolean isEmpty) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.databaseIndex = databaseIndex;
		this.isEmpty = isEmpty;
	}

	public int getDatabaseIndex() {
		return databaseIndex;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height;
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		manager.minecraft.renderEngine.bindTexture(TEXTURE_LOCATION);
		Drawable texture = SLOT;
		if (isSelected()) {
			texture = SLOT_SELECTED;
		}
		texture.draw(startX + xPos - 3, startY + yPos - 3);
		ItemStack itemStack = getItemStack();
		if (!itemStack.isEmpty()) {
			Minecraft minecraft = Minecraft.getMinecraft();
			TextureManager textureManager = minecraft.getTextureManager();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.enableGUIStandardItemLighting();
			GuiUtil.drawItemStack(manager.gui, itemStack, startX + xPos, startY + yPos);
			RenderHelper.disableStandardItemLighting();
		}
		if (mouseOver) {
			drawMouseOver();
		}
	}

	private void drawMouseOver() {
		GlStateManager.disableDepth();
		GlStateManager.colorMask(true, true, true, false);
		manager.gui.drawGradientRect(xPos, yPos, xPos + width, yPos + height, -2130706433, -2130706433);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableDepth();
	}

	@Override
	public void update(int mouseX, int mouseY) {
		mouseOver = isMouseOver(mouseX, mouseY);
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0 && mouseButton != 1 && mouseButton != 2 || !manager.minecraft.player.inventory.getItemStack().isEmpty()) {
			return;
		}
		GuiDatabase gui = (GuiDatabase) manager.gui;
		DatabaseItem item = gui.getItem(databaseIndex);
		if (item == null) {
			return;
		}

		if (GuiScreen.isCtrlKeyDown() && mouseButton == 0) {
			gui.analyzer.setSelectedSlot(databaseIndex);
			return;
		}

		ignoreMouseUp = true;
		byte flags = 0;
		if (GuiScreen.isShiftKeyDown()) {
			flags |= PacketExtractItem.SHIFT;
		}
		if (mouseButton == 1) {
			flags |= PacketExtractItem.HALF;
		} else if (mouseButton == 2 && manager.minecraft.player.capabilities.isCreativeMode) {
			flags |= PacketExtractItem.CLONE;
		}
		NetworkUtil.sendToServer(new PacketExtractItem(item.invIndex, flags));
	}

	@Override
	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		if (!isMouseOver(mouseX, mouseY)
			|| ignoreMouseUp
			|| eventType != 0 && eventType != 1
			|| manager.minecraft.player.inventory.getItemStack().isEmpty()) {
			ignoreMouseUp = false;
			return false;
		}
		GuiDatabase gui = (GuiDatabase) manager.gui;
		DatabaseItem item = gui.getItem(databaseIndex);
		if (item == null) {
			return false;
		}

		NetworkUtil.sendToServer(new PacketInsertItem(eventType == 1));
		return true;
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		ItemStack itemStack = getItemStack();
		ToolTip tip = new ToolTip();
		if (!itemStack.isEmpty()) {
			tip.add(ItemTooltipUtil.getInformation(itemStack));
		}
		return tip;
	}

	public boolean isSelected() {
		if (!isEmpty) {
			return false;
		}
		DatabaseItem slotItem = ((GuiDatabase) manager.gui).getItem(databaseIndex);
		DatabaseItem selectedItem = ((GuiDatabase) manager.gui).getSelectedItem();
		return slotItem != null && slotItem.equals(selectedItem);
	}

	public ItemStack getItemStack() {
		if (!isEmpty) {
			return ItemStack.EMPTY;
		}
		DatabaseItem item = ((GuiDatabase) manager.gui).getItem(databaseIndex);
		if (item == null) {
			return ItemStack.EMPTY;
		}
		return item.itemStack;
	}
}
