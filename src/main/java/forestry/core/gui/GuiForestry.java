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

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import forestry.core.config.Config;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.slots.SlotForestry;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.tooltips.ToolTipLine;
import forestry.core.gui.widgets.Widget;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.IOwnable;
import forestry.core.interfaces.IPowerHandler;
import forestry.core.proxy.Proxies;
import forestry.core.utils.FontColour;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public abstract class GuiForestry<T extends TileForestry> extends GuiContainer implements INEIGuiHandler {

	/* WIDGETS */
	protected final WidgetManager widgetManager;
	/* LEDGERS */
	protected final LedgerManager ledgerManager;
	protected final T tile;
	protected final FontColour fontColor;
	public final ResourceLocation textureFile;

	public GuiForestry(String texture, ContainerForestry container) {
		this(new ResourceLocation("forestry", texture), container, null);
	}

	public GuiForestry(String texture, ContainerForestry container, Object inventory) {
		this(new ResourceLocation("forestry", texture), container, inventory);
	}

	public GuiForestry(ResourceLocation texture, ContainerForestry container) {
		this(texture, container, null);
	}

	@SuppressWarnings("unchecked")
	public GuiForestry(ResourceLocation texture, ContainerForestry container, Object inventory) {
		super(container);
		this.widgetManager = new WidgetManager(this);
		this.ledgerManager = new LedgerManager(this);

		this.textureFile = texture;
		this.inventorySlots = container;

		if (inventory instanceof TileForestry)
			this.tile = (T) inventory;
		else
			this.tile = null;

		fontColor = new FontColour(Proxies.common.getSelectedTexturePack(Proxies.common.getClientInstance()));
		initLedgers(inventory);
	}

	/* LEDGERS */
	protected void initLedgers(Object inventory) {

		if (inventory instanceof IErrorSource && ((IErrorSource) inventory).throwsErrors())
			ledgerManager.add(new ErrorLedger(ledgerManager, (IErrorSource) inventory));

		if (inventory instanceof IClimatised && ((IClimatised) inventory).isClimatized())
			ledgerManager.add(new ClimateLedger(ledgerManager, (IClimatised) inventory));

		if (!Config.disableEnergyStat && inventory instanceof IPowerHandler && ((IPowerHandler) inventory).getEnergyManager().getMaxEnergyStored() > 0)
			ledgerManager.add(new PowerLedger(ledgerManager, (IPowerHandler) inventory));

		if (!Config.disableHints && inventory instanceof IHintSource && ((IHintSource) inventory).hasHints())
			ledgerManager.add(new HintLedger(ledgerManager, (IHintSource) inventory));

		if (inventory instanceof IOwnable && ((IOwnable) inventory).isOwnable())
			ledgerManager.add(new OwnerLedger(ledgerManager, (IOwnable) inventory));

	}

	/* TEXT HELPER FUNCTIONS */
	protected int column0;
	protected int column1;
	protected int column2;
	private int line;
	protected float factor = 0.75f;

	protected final void setFactor(float factor) {
		this.factor = factor;
	}

	protected final void startPage() {
		line = 12;
		GL11.glPushMatrix();
		GL11.glScalef(factor, factor, factor);
	}

	protected final void startPage(int column0, int column1, int column2) {

		this.column0 = column0;
		this.column1 = column1;
		this.column2 = column2;

		startPage();
	}

	protected final int adjustToFactor(int fixed) {
		return (int) (fixed * (1 / factor));
	}

	protected final int getLineY() {
		return line;
	}

	protected final void newLine() {
		line += 12 * factor;
	}

	protected final void newLine(int lineHeight) {
		line += lineHeight * factor;
	}

	protected final void endPage() {
		GL11.glPopMatrix();
	}

	protected final void drawRow(String text0, String text1, String text2, int colour0, int colour1, int colour2) {
		drawLine(text0, column0, colour0);
		drawLine(text1, column1, colour1);
		drawLine(text2, column2, colour2);
		newLine();
	}

	protected final void drawLine(String text, int x) {
		drawLine(text, x, fontColor.get("gui.screen"));
	}

	protected final void drawSplitLine(String text, int x, int maxWidth) {
		drawSplitLine(text, x, maxWidth, fontColor.get("gui.screen"));
	}

	protected final void drawCenteredLine(String text, int x, int width) {
		drawCenteredLine(text, x, width, fontColor.get("gui.screen"));
	}

	protected final void drawCenteredLine(String text, int x, int width, int color) {
		fontRendererObj.drawString(text, (int) ((guiLeft + x) * (1 / factor)) + (adjustToFactor(width) - fontRendererObj.getStringWidth(text)) / 2,
				(int) ((guiTop + line) * (1 / factor)), color);
	}

	protected final void drawLine(String text, int x, int color) {
		fontRendererObj.drawString(text, (int) ((guiLeft + x) * (1 / factor)), (int) ((guiTop + line) * (1 / factor)), color);
	}

	protected final void drawSplitLine(String text, int x, int maxWidth, int color) {
		fontRendererObj.drawSplitString(text, (int) ((guiLeft + x) * (1 / factor)), (int) ((guiTop + line) * (1 / factor)), (int) (maxWidth * (1 / factor)), color);
	}

	/* CORE GUI HANDLING */
	protected int getCenteredOffset(String string) {
		return getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
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

		Slot slot = getSlotAtPosition(mouseX, mouseY);
		if (mouseButton == 1 && slot instanceof SlotForestry && ((SlotForestry) slot).isPhantom())
			return;
		super.mouseClickMove(mouseX, mouseY, mouseButton, time);
	}

	public Slot getSlotAtPosition(int par1, int par2) {
		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
			Slot slot = (Slot) this.inventorySlots.inventorySlots.get(k);

			if (isMouseOverSlot(slot, par1, par2)) {
				return slot;
			}
		}

		return null;
	}

	public boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
		return this.func_146978_c(par1Slot.xDisplayPosition, par1Slot.yDisplayPosition, 16, 16, par2, par3);
	}

	public void drawToolTips(ToolTip toolTips, int mouseX, int mouseY) {
		if (toolTips == null)
			return;
		if (toolTips.isEmpty())
			return;

		int left = this.guiLeft;
		int top = this.guiTop;
		int length = 0;
		int height = 0;
		int x;
		int y;

		for (ToolTipLine tip : toolTips) {
			y = this.fontRendererObj.getStringWidth(tip.getText());

			height += 10 + tip.getSpacing();
			if (y > length)
				length = y;
		}

		x = mouseX - left + 12;
		y = mouseY - top - 12;

		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		Color backgroundColor = new Color(16, 0, 16, 240);
		int backgroundColorInt = backgroundColor.getRGB();
		this.drawGradientRect(x - 3, y - 4, x + length + 2, y - 3, backgroundColorInt, backgroundColorInt);
		this.drawGradientRect(x - 3, y + height + 1, x + length + 2, y + height + 2, backgroundColorInt, backgroundColorInt);
		this.drawGradientRect(x - 3, y - 3, x + length + 2, y + height + 1, backgroundColorInt, backgroundColorInt);
		this.drawGradientRect(x - 4, y - 3, x - 3, y + height + 1, backgroundColorInt, backgroundColorInt);
		this.drawGradientRect(x + length + 2, y - 3, x + length + 3, y + height + 1, backgroundColorInt, backgroundColorInt);

		Color borderColorTop = new Color(80, 0, 255, 80);
		int borderColorTopInt = borderColorTop.getRGB();
		Color borderColorBottom = new Color((borderColorTopInt & 0xfefefe) >> 1 | borderColorTopInt & -0x1000000, true);
		int borderColorBottomInt = borderColorBottom.getRGB();
		this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height, borderColorTopInt, borderColorBottomInt);
		this.drawGradientRect(x + length + 1, y - 3 + 1, x + length + 2, y + height, borderColorTopInt, borderColorBottomInt);
		this.drawGradientRect(x - 3, y - 3, x + length + 2, y - 3 + 1, borderColorTopInt, borderColorTopInt);
		this.drawGradientRect(x - 3, y + height, x + length + 2, y + height + 1, borderColorBottomInt, borderColorBottomInt);

		for (ToolTipLine tip : toolTips) {
			String line = tip.getText();

			if (tip.getColor() == null)
				line = "\u00a77" + line;
			else
				line = "\u00a7" + tip.getColor().getFormattingCode() + line;

			this.fontRendererObj.drawStringWithShadow(line, x, y, -1);

			y += 10 + tip.getSpacing();
		}

		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240 / 1.0F, 240 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		ledgerManager.drawTooltips(mouseX, mouseY);

		InventoryPlayer playerInv = mc.thePlayer.inventory;

		if (playerInv.getItemStack() == null) {
			drawToolTips(widgetManager.widgets, mouseX, mouseY);
			drawToolTips(buttonList, mouseX, mouseY);
			drawToolTips(inventorySlots.inventorySlots, mouseX, mouseY);
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void drawToolTips(Collection<?> objects, int mouseX, int mouseY) {
		for (Object obj : objects) {
			if (!(obj instanceof IToolTipProvider))
				continue;
			IToolTipProvider provider = (IToolTipProvider) obj;
			if (!provider.isToolTipVisible())
				continue;
			ToolTip tips = provider.getToolTip();
			if (tips == null)
				continue;
			boolean mouseOver = provider.isMouseOver(mouseX - guiLeft, mouseY - guiTop);
			tips.onTick(mouseOver);
			if (mouseOver && tips.isReady()) {
				tips.refresh();
				drawToolTips(tips, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(textureFile);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int left = this.guiLeft;
		int top = this.guiTop;

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glTranslatef(left, top, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240 / 1.0F, 240 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		drawWidgets();

		GL11.glPopMatrix();
		GL11.glPopAttrib();

		bindTexture(textureFile);
	}

	protected void drawWidgets() {
		ledgerManager.drawLedgers();
		widgetManager.drawWidgets();
	}

	protected void bindTexture(ResourceLocation texturePath) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Proxies.common.bindTexture(texturePath);
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
	//
	//	protected void drawGuiContainerForegroundLayer() {
	//	}

	/**
	 * Draws the basic background texture centered on the screen.
	 */
	//	protected void drawBackground() {
	//		drawBackground((this.width - this.xSize) / 2, (this.height - this.ySize) / 2, this.xSize, this.ySize);
	//	}
	//	protected void drawBackground(int x, int y, int w, int h) {
	//		bindTexture();
	//		this.drawTexturedModalRect(x, y, 0, 0, w, h);
	//	}
	//	protected void bindTexture() {
	//		bindTexture(textureFile);
	//	}
	//
	//	protected void drawSlotInventory(Slot slot) {
	//
	//		int xPos = slot.xDisplayPosition;
	//		int yPos = slot.yDisplayPosition;
	//		ItemStack slotStack = slot.getStack();
	//		boolean backgroundDrawn = false;
	//
	//		this.zLevel = 100.0F;
	//		itemRenderer.zLevel = 100.0F;
	//
	//		if (slotStack == null) {
	//			IIcon icon = slot.getBackgroundIconIndex();
	//
	//			if (icon != null) {
	//				GL11.glDisable(GL11.GL_LIGHTING);
	//				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
	//				Proxies.common.bindTexture(SpriteSheet.ITEMS);
	//				this.drawTexturedModelRectFromIcon(xPos, yPos, icon, 16, 16);
	//				GL11.glEnable(GL11.GL_LIGHTING);
	//				backgroundDrawn = true;
	//			}
	//		}
	//
	//		if (!backgroundDrawn && slotStack != null)
	//			drawItemStack(slotStack, xPos, yPos);
	//
	//		this.zLevel = 0.0F;
	//		itemRenderer.zLevel = 0.0F;
	//	}
	public void drawItemStack(ItemStack stack, int xPos, int yPos) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		FontRenderer font = null;
		if (stack != null)
			font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRendererObj;
		itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), stack, xPos, yPos);
		itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), stack, xPos, yPos);
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	protected class ItemStackWidget extends Widget {
		ItemStack itemStack;

		public ItemStackWidget(int xPos, int yPos, ItemStack itemStack) {
			super(widgetManager, xPos, yPos);

			IIcon icon = itemStack.getItem().getIcon(itemStack, 0);

			this.width = icon.getIconWidth();
			this.height = icon.getIconHeight();
			this.itemStack = itemStack;
		}

		@Override
		public void draw(int startX, int startY) {
			itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, xPos + startX, yPos + startY);
			itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, itemStack, xPos + startX, yPos + startY);
		}

		@Override
		public ToolTip getToolTip() {
			ToolTip tip = new ToolTip();
			tip.add(itemStack.getDisplayName());
			return tip;
		}
	}

	/* NEI */
	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) { return null; }

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) { return null; }

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) { return null; }

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) { return false; }

	@Override
	@Optional.Method(modid = "NotEnoughItems")
	public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
		if (gui instanceof GuiForestry)
			return ((GuiForestry)gui).ledgerManager.ledgerOverlaps(x, y, w, h);
		else
			return false;
	}
}
