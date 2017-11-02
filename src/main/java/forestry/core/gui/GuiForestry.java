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

import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorSource;
import forestry.core.config.Config;
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.ledgers.HintLedger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.gui.ledgers.OwnerLedger;
import forestry.core.gui.ledgers.PowerLedger;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.owner.IOwnedTile;
import forestry.core.render.ColourProperties;
import forestry.core.render.ForestryResource;
import forestry.core.tiles.IClimatised;
import forestry.energy.EnergyManager;

public abstract class GuiForestry<C extends Container> extends GuiContainer {
	protected final C container;

	public final ResourceLocation textureFile;
	protected final WidgetManager widgetManager;
	protected final LedgerManager ledgerManager;
	protected final TextLayoutHelper textLayout;

	protected GuiForestry(String texture, C container) {
		this(new ForestryResource(texture), container);
	}

	protected GuiForestry(ResourceLocation texture, C container) {
		super(container);

		this.widgetManager = new WidgetManager(this);
		this.ledgerManager = new LedgerManager(this);

		this.textureFile = texture;

		this.container = container;

		this.textLayout = new TextLayoutHelper(this, ColourProperties.INSTANCE);
	}

	/* LEDGERS */
	@Override
	public void initGui() {
		super.initGui();

		int maxLedgerWidth = (this.width - this.xSize) / 2;

		this.ledgerManager.setMaxWidth(maxLedgerWidth);
		this.ledgerManager.clear();

		addLedgers();
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	protected abstract void addLedgers();

	protected final void addErrorLedger(IErrorSource errorSource) {
		ledgerManager.add(errorSource);
	}

	protected final void addErrorLedger(IErrorLogicSource errorSource) {
		ledgerManager.add(errorSource.getErrorLogic());
	}

	protected final void addClimateLedger(IClimatised climatised) {
		ledgerManager.add(new ClimateLedger(ledgerManager, climatised));
	}

	protected final void addPowerLedger(EnergyManager energyManager) {
		if (Config.enableEnergyStat) {
			ledgerManager.add(new PowerLedger(ledgerManager, energyManager));
		}
	}

	protected final void addHintLedger(String hintsKey) {
		if (Config.enableHints) {
			List<String> hints = Config.hints.get(hintsKey);
			addHintLedger(hints);
		}
	}

	protected final void addHintLedger(List<String> hints) {
		if (Config.enableHints) {
			if (!hints.isEmpty()) {
				ledgerManager.add(new HintLedger(ledgerManager, hints));
			}
		}
	}

	protected final void addOwnerLedger(IOwnedTile ownedTile) {
		ledgerManager.add(new OwnerLedger(ledgerManager, ownedTile));
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ledgerManager.onGuiClosed();
	}

	public ColourProperties getFontColor() {
		return ColourProperties.INSTANCE;
	}

	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	@Override
	protected void mouseClicked(int xPos, int yPos, int mouseButton) throws IOException {
		super.mouseClicked(xPos, yPos, mouseButton);

		// / Handle ledger clicks
		ledgerManager.handleMouseClicked(xPos, yPos, mouseButton);
		widgetManager.handleMouseClicked(xPos, yPos, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (widgetManager.handleMouseRelease(mouseX, mouseY, state)) {
			return;
		}
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long time) {

		widgetManager.handleMouseMove(mouseX, mouseY, mouseButton, time);

		super.mouseClickMove(mouseX, mouseY, mouseButton, time);
	}

	@Nullable
	public FluidStack getFluidStackAtPosition(int mouseX, int mouseY) {
		for (Widget widget : widgetManager.getWidgets()) {
			if (widget instanceof TankWidget && widget.isMouseOver(mouseX - guiLeft, mouseY - guiTop)) {
				TankWidget tankWidget = (TankWidget) widget;
				IFluidTank tank = tankWidget.getTank();
				if (tank != null) {
					return tank.getFluid();
				}
			}
		}
		return null;
	}

	@Nullable
	protected Slot getSlotAtPosition(int mouseX, int mouseY) {
		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
			Slot slot = this.inventorySlots.inventorySlots.get(k);

			if (isMouseOverSlot(slot, mouseX, mouseY)) {
				return slot;
			}
		}

		return null;
	}

	private boolean isMouseOverSlot(Slot par1Slot, int mouseX, int mouseY) {
		return isPointInRegion(par1Slot.xPos, par1Slot.yPos, 16, 16, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ledgerManager.drawTooltips(mouseX, mouseY);

		InventoryPlayer playerInv = mc.player.inventory;

		if (playerInv.getItemStack().isEmpty()) {
			GuiUtil.drawToolTips(this, widgetManager.getWidgets(), mouseX, mouseY);
			GuiUtil.drawToolTips(this, buttonList, mouseX, mouseY);
			GuiUtil.drawToolTips(this, inventorySlots.inventorySlots, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		drawBackground();

		widgetManager.updateWidgets(mouseX - guiLeft, mouseY - guiTop);

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(guiLeft, guiTop, 0.0F);
			drawWidgets();
		}
		GlStateManager.popMatrix();

		bindTexture(textureFile);
	}

	protected void drawBackground(){
		bindTexture(textureFile);

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	protected void drawWidgets() {
		ledgerManager.drawLedgers();
		widgetManager.drawWidgets();
	}

	protected void bindTexture(ResourceLocation texturePath) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(texturePath);
	}

	public void setZLevel(float level) {
		this.zLevel = level;
	}

	public int getSizeX() {
		return xSize;
	}

	public int getSizeY() {
		return ySize;
	}

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
		super.drawGradientRect(par1, par2, par3, par4, par5, par6);
	}

	public List<Rectangle> getExtraGuiAreas() {
		return ledgerManager.getLedgerAreas();
	}

	public TextLayoutHelper getTextLayout() {
		return textLayout;
	}
}
