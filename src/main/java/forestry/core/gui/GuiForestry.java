/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.Optional;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorSource;
import forestry.core.access.IRestrictedAccess;
import forestry.core.config.Config;
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.ledgers.HintLedger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.gui.ledgers.OwnerLedger;
import forestry.core.gui.ledgers.PowerLedger;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.FontColour;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.IPowerHandler;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public abstract class GuiForestry<C extends Container, I extends IInventory> extends GuiContainer implements INEIGuiHandler {
	protected final I inventory;
	protected final C container;

	public final ResourceLocation textureFile;
	protected final WidgetManager widgetManager;
	protected LedgerManager ledgerManager;
	protected TextLayoutHelper textLayout;
	protected FontColour fontColor;

	protected GuiForestry(String texture, C container, I inventory) {
		this(new ResourceLocation("forestry", texture), container, inventory);
	}

	protected GuiForestry(ResourceLocation texture, C container, I inventory) {
		super(container);

		this.widgetManager = new WidgetManager(this);

		this.textureFile = texture;

		this.inventory = inventory;
		this.container = container;

		this.fontColor = new FontColour(Proxies.render.getSelectedTexturePack());
		this.textLayout = new TextLayoutHelper(this, this.fontColor);
	}

	@Override
	public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
		super.setWorldAndResolution(minecraft, width, height);
		textLayout.setFontRendererObj(fontRendererObj);
	}

	/* LEDGERS */
	@Override
	public void initGui() {
		super.initGui();

		int maxLedgerWidth = (this.width - this.xSize) / 2;
		this.ledgerManager = new LedgerManager(this, maxLedgerWidth);

		addLedgers();
	}

	protected void addLedgers() {
		if (inventory instanceof IErrorSource) {
			ledgerManager.add((IErrorSource) inventory);
		}

		if (inventory instanceof IErrorLogicSource) {
			IErrorLogicSource errorLogicSource = (IErrorLogicSource) inventory;
			ledgerManager.add(errorLogicSource.getErrorLogic());
		}

		if (inventory instanceof IClimatised) {
			ledgerManager.add(new ClimateLedger(ledgerManager, (IClimatised) inventory));
		}

		if (Config.enableEnergyStat && inventory instanceof IPowerHandler && ((IPowerHandler) inventory).getEnergyManager().getMaxEnergyStored() > 0) {
			ledgerManager.add(new PowerLedger(ledgerManager, (IPowerHandler) inventory));
		}

		if (Config.enableHints && inventory instanceof IHintSource) {
			IHintSource hintSource = (IHintSource) inventory;
			List<String> hints = hintSource.getHints();
			if (hints != null && hints.size() > 0) {
				ledgerManager.add(new HintLedger(ledgerManager, hintSource));
			}
		}

		if (inventory instanceof IRestrictedAccess) {
			ledgerManager.add(new OwnerLedger(ledgerManager, (IRestrictedAccess) inventory));
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ledgerManager.onGuiClosed();
	}

	public FontColour getFontColor() {
		return fontColor;
	}

	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}

	@Override
	protected void mouseClicked(int xPos, int yPos, int mouseButton) {
		super.mouseClicked(xPos, yPos, mouseButton);

		// / Handle ledger clicks
		ledgerManager.handleMouseClicked(xPos, yPos, mouseButton);
		widgetManager.handleMouseClicked(xPos, yPos, mouseButton);
	}

	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int eventType) {
		super.mouseMovedOrUp(mouseX, mouseY, eventType);

		widgetManager.handleMouseRelease(mouseX, mouseY, eventType);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long time) {

		widgetManager.handleMouseMove(mouseX, mouseY, mouseButton, time);

		super.mouseClickMove(mouseX, mouseY, mouseButton, time);
	}

	protected Slot getSlotAtPosition(int par1, int par2) {
		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
			Slot slot = (Slot) this.inventorySlots.inventorySlots.get(k);

			if (isMouseOverSlot(slot, par1, par2)) {
				return slot;
			}
		}

		return null;
	}

	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
		return this.func_146978_c(par1Slot.xDisplayPosition, par1Slot.yDisplayPosition, 16, 16, par2, par3);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		{
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240 / 1.0F, 240 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			ledgerManager.drawTooltips(mouseX, mouseY);

			InventoryPlayer playerInv = mc.thePlayer.inventory;

			if (playerInv.getItemStack() == null) {
				GuiUtil.drawToolTips(this, widgetManager.getWidgets(), mouseX, mouseY);
				GuiUtil.drawToolTips(this, buttonList, mouseX, mouseY);
				GuiUtil.drawToolTips(this, inventorySlots.inventorySlots, mouseX, mouseY);
			}
		}
		GL11.glPopAttrib();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture(textureFile);

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		{
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glPushMatrix();
			{
				GL11.glTranslatef(guiLeft, guiTop, 0.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240 / 1.0F, 240 / 1.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

				drawWidgets();
			}
			GL11.glPopMatrix();
		}
		GL11.glPopAttrib();

		bindTexture(textureFile);
	}

	protected void drawWidgets() {
		ledgerManager.drawLedgers();
		widgetManager.drawWidgets();
	}

	protected void bindTexture(ResourceLocation texturePath) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Proxies.render.bindTexture(texturePath);
	}

	public void setZLevel(float level) {
		this.zLevel = level;
	}

	public static RenderItem getItemRenderer() {
		return itemRender;
	}

	public int getSizeX() {
		return xSize;
	}

	public int getSizeY() {
		return ySize;
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
		super.drawGradientRect(par1, par2, par3, par4, par5, par6);
	}

	/* NEI */
	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
		return null;
	}

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
		return Collections.emptyList();
	}

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
		return Collections.emptyList();
	}

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
		return false;
	}

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
		if (gui instanceof GuiForestry) {
			return ((GuiForestry) gui).ledgerManager.ledgerOverlaps(x, y, w, h);
		} else {
			return false;
		}
	}
}
