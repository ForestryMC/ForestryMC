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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.api.climate.IClimatised;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorSource;
import forestry.core.config.Config;
import forestry.core.gui.elements.WindowGui;
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.ledgers.HintLedger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.gui.ledgers.OwnerLedger;
import forestry.core.gui.ledgers.PowerLedger;
import forestry.core.gui.slots.ISlotTextured;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.owner.IOwnedTile;
import forestry.core.render.ColourProperties;
import forestry.core.render.ForestryResource;
import forestry.energy.EnergyManager;

public abstract class GuiForestry<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> implements IGuiSizable {
	protected final C container;

	public final ResourceLocation textureFile;
	protected final WidgetManager widgetManager;
	protected final LedgerManager ledgerManager;
	protected final TextLayoutHelper textLayout;
	protected final WindowGui<?> window;

	protected GuiForestry(String texture, C container, Inventory inv, Component title) {
		this(new ForestryResource(texture), container, inv, title);
	}

	protected GuiForestry(ResourceLocation texture, C container, Inventory inv, Component title) {
		super(container, inv, title);

		this.widgetManager = new WidgetManager(this);
		this.ledgerManager = new LedgerManager(this);
		this.window = new WindowGui<>(imageWidth, imageHeight, this);

		this.textureFile = texture;

		this.container = container;

		this.textLayout = new TextLayoutHelper(this, ColourProperties.INSTANCE);
	}

	/* LEDGERS */
	@Override
	public void init() {
		super.init();

		int maxLedgerWidth = (this.width - this.imageWidth) / 2;

		this.ledgerManager.setMaxWidth(maxLedgerWidth);
		this.ledgerManager.clear();

		this.window.init(leftPos, topPos);

		addLedgers();
	}

	@Override
	public void resize(Minecraft mc, int width, int height) {
		window.setSize(width, height);
		super.resize(mc, width, height);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		window.updateClient();
	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
		window.setMousePosition(mouseX, mouseY);
		this.renderBackground(transform);
		super.render(transform, mouseX, mouseY, partialTicks);
		renderTooltip(transform, mouseX, mouseY);
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
	public void onClose() {
		super.onClose();
		ledgerManager.onClose();
	}

	public ColourProperties getFontColor() {
		return ColourProperties.INSTANCE;
	}

	public Font getFontRenderer() {
		return minecraft.font;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		// / Handle ledger clicks
		ledgerManager.handleMouseClicked(mouseX, mouseY, mouseButton);
		widgetManager.handleMouseClicked(mouseX, mouseY, mouseButton);
		if (window.onMouseClicked(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (widgetManager.handleMouseRelease(mouseX, mouseY, mouseButton)
				|| window.onMouseReleased(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		window.onMouseMove(mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double oldMouseX, double oldMouseY) {
		if (window.onMouseDrag(mouseX, mouseY)) {
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, mouseButton, oldMouseX, oldMouseY);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		/*InputMappings.Input mouseKey = InputMappings.getKey(key, scanCode);
		if (key == GLFW.GLFW_KEY_ESCAPE || this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
			this.minecraft.player.closeContainer();
			return true;
		}*/
		if (window.onKeyPressed(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int key, int scanCode, int modifiers) {
		if (window.onKeyReleased(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyReleased(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (window.onCharTyped(codePoint, modifiers)) {
			return true;
		}
		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double deltaWheel) {
		super.mouseScrolled(mouseX, mouseY, deltaWheel);
		if (deltaWheel != 0) {
			if (window.onMouseScrolled(mouseX, mouseY, deltaWheel)) {
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, deltaWheel);
	}

	@Nullable
	public FluidStack getFluidStackAtPosition(double mouseX, double mouseY) {
		for (Widget widget : widgetManager.getWidgets()) {
			if (widget instanceof TankWidget tankWidget && widget.isMouseOver(mouseX - leftPos, mouseY - topPos)) {
				IFluidTank tank = tankWidget.getTank();
				if (tank != null) {
					return tank.getFluid();
				}
			}
		}
		return null;
	}

	@Nullable
	protected Slot getSlotAtPosition(double mouseX, double mouseY) {
		for (int k = 0; k < this.container.slots.size(); ++k) {
			Slot slot = this.container.slots.get(k);

			if (isMouseOverSlot(slot, mouseX, mouseY)) {
				return slot;
			}
		}

		return null;
	}

	private boolean isMouseOverSlot(Slot par1Slot, double mouseX, double mouseY) {
		return isHovering(par1Slot.x, par1Slot.y, 16, 16, mouseX, mouseY);
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
		return !window.isMouseOver(mouseX - guiLeft, mouseY - guiTop) && super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton);
	}

	@Override
	public void renderSlot(PoseStack transform, Slot slot) {
		if (slot instanceof ISlotTextured textured) {
			ItemStack stack = slot.getItem();
			if (stack.isEmpty() && slot.isActive()) {
				ResourceLocation location = textured.getBackgroundTexture();
				if (location != null) {
					TextureAtlasSprite sprite = textured.getBackgroundAtlas().apply(location);
					RenderSystem.setShaderTexture(0, sprite.atlas().location());
					blit(transform, slot.x, slot.y, this.getBlitOffset(), 16, 16, sprite);
				}
			}
		}
		super.renderSlot(transform, slot);
	}


	@Override
	protected void renderLabels(PoseStack transform, int mouseX, int mouseY) {
		ledgerManager.drawTooltips(transform, mouseX, mouseY);

		if (this.menu.getCarried().isEmpty()) {
			GuiUtil.drawToolTips(transform, this, widgetManager.getWidgets(), mouseX, mouseY);
			GuiUtil.drawToolTips(transform, this, this.renderables, mouseX, mouseY);
			GuiUtil.drawToolTips(transform, this, container.slots, mouseX, mouseY);
			window.drawTooltip(transform, mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		drawBackground(transform);

		widgetManager.updateWidgets(mouseX - leftPos, mouseY - topPos);

		//RenderHelper.enableGUIStandardItemLighting(); //TODO: Is there an replacement ?
		// RenderSystem.disableLighting();
		// RenderSystem.enableRescaleNormal();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		transform.pushPose();
		{
			transform.translate(leftPos, topPos, 0.0F);
			drawWidgets(transform);
		}
		transform.popPose();

		// RenderSystem.color3f(1.0F, 1.0F, 1.0F);

		window.draw(transform, mouseX, mouseY);

		bindTexture(textureFile);
	}

	protected void drawBackground(PoseStack transform) {
		bindTexture(textureFile);

		blit(transform, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	protected void drawWidgets(PoseStack transform) {
		ledgerManager.drawLedgers(transform);
		widgetManager.drawWidgets(transform);
	}

	protected void bindTexture(ResourceLocation texturePath) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texturePath);
	}

	@Override
	public int getSizeX() {
		return imageWidth;
	}

	@Override
	public int getSizeY() {
		return imageHeight;
	}

	@Override
	public int getGuiLeft() {
		return leftPos;
	}

	@Override
	public int getGuiTop() {
		return topPos;
	}

	@Override
	public Minecraft getGameInstance() {
		return Preconditions.checkNotNull(minecraft);
	}

	public List<Rect2i> getExtraGuiAreas() {
		return ledgerManager.getLedgerAreas();
	}

	public TextLayoutHelper getTextLayout() {
		return textLayout;
	}
}
