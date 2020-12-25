/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorSource;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.gui.elements.Window;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.events.GuiEvent;
import forestry.core.gui.elements.lib.events.GuiEventDestination;
import forestry.core.gui.ledgers.*;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.owner.IOwnedTile;
import forestry.core.render.ColourProperties;
import forestry.core.tiles.IClimatised;
import forestry.energy.EnergyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.List;

public abstract class GuiForestry<C extends Container> extends ContainerScreen<C> implements IGuiSizable {
    public final ResourceLocation textureFile;
    protected final C container;
    protected final WidgetManager widgetManager;
    protected final LedgerManager ledgerManager;
    protected final TextLayoutHelper textLayout;
    protected final Window<?> window;

    protected GuiForestry(String texture, C container, PlayerInventory inv, ITextComponent title) {
        this(new ResourceLocation(Constants.MOD_ID, texture), container, inv, title);
    }

    protected GuiForestry(ResourceLocation texture, C container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        this.widgetManager = new WidgetManager(this);
        this.ledgerManager = new LedgerManager(this);
        this.window = new Window<>(xSize, ySize, this);

        this.textureFile = texture;

        this.container = container;

        this.textLayout = new TextLayoutHelper(this, ColourProperties.INSTANCE);
    }

    /* LEDGERS */
    @Override
    public void init() {
        super.init();

        int maxLedgerWidth = (this.width - this.xSize) / 2;

        this.ledgerManager.setMaxWidth(maxLedgerWidth);
        this.ledgerManager.clear();

        this.window.init(guiLeft, guiTop);

        addLedgers();
    }

    @Override
    public void init(Minecraft mc, int width, int height) {
        window.setSize(width, height);
        super.init(mc, width, height);
    }

    @Override
    public void tick() {
        window.updateClient();
    }

    //TODO - I think this is the right method
    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        window.setMousePosition(mouseX, mouseY);
        this.renderBackground(transform);
        super.render(transform, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(transform, mouseX, mouseY);
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

    public FontRenderer getFontRenderer() {
        return minecraft.fontRenderer;
    }

    //super has double double int
    //int is probably mousebutton?
    //TODO - check params
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Handle ledger clicks
        ledgerManager.handleMouseClicked(mouseX, mouseY, mouseButton);
        widgetManager.handleMouseClicked(mouseX, mouseY, mouseButton);
        IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
        window.postEvent(new GuiEvent.DownEvent(origin, mouseX, mouseY, mouseButton), GuiEventDestination.ALL);

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (widgetManager.handleMouseRelease(mouseX, mouseY, mouseButton)) {
            return true;
        }

        IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
        window.postEvent(new GuiEvent.UpEvent(origin, mouseX, mouseY, mouseButton), GuiEventDestination.ALL);
        super.mouseReleased(mouseX, mouseY, mouseButton);

        return true;
    }

//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
//        if (scanCode == GLFW.GLFW_KEY_ESCAPE || this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
//            this.minecraft.player.closeScreen();
//            return true;
//        }
//        IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
//        window.postEvent(new GuiEvent.KeyEvent(origin, keyCode, scanCode, modifiers), GuiEventDestination.ALL);
//        return true;
//    }
//
//    @Override
//    public boolean charTyped(char codePoint, int modifiers) {
//        InputMappings.Input mouseKey = InputMappings.getInputByCode(codePoint, modifiers);
//        if (modifiers == GLFW.GLFW_KEY_ESCAPE || this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
//            this.minecraft.player.closeScreen();
//            return true;
//        }
//
//        IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
//        window.postEvent(new GuiEvent.CharEvent(origin, codePoint, modifiers), GuiEventDestination.ALL);
//        return true;
//    }

    @Nullable
    public FluidStack getFluidStackAtPosition(double mouseX, double mouseY) {
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
    protected Slot getSlotAtPosition(double mouseX, double mouseY) {
        for (int k = 0; k < this.container.inventorySlots.size(); ++k) {
            Slot slot = this.container.inventorySlots.get(k);

            if (isMouseOverSlot(slot, mouseX, mouseY)) {
                return slot;
            }
        }

        return null;
    }

    private boolean isMouseOverSlot(Slot par1Slot, double mouseX, double mouseY) {
        return isPointInRegion(par1Slot.xPos, par1Slot.yPos, 16, 16, mouseX, mouseY);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int idk) {
        return !window.isMouseOver(mouseX, mouseY) && super.hasClickedOutside(
                mouseX,
                mouseY,
                guiLeft,
                guiTop,
                0
        );    //TODO - I have no idea what the last param actually does
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack transform, int mouseX, int mouseY) {
        ledgerManager.drawTooltips(transform, mouseY, mouseX);

        if (this.playerInventory.getItemStack().isEmpty()) {
            GuiUtil.drawToolTips(transform, this, widgetManager.getWidgets(), mouseX, mouseY);
            GuiUtil.drawToolTips(transform, this, this.buttons, mouseX, mouseY);
            GuiUtil.drawToolTips(transform, this, container.inventorySlots, mouseX, mouseY);
            window.drawTooltip(transform, mouseY, mouseX);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseX, int mouseY) {
        drawBackground(transform);

        widgetManager.updateWidgets(mouseX - guiLeft, mouseY - guiTop);

        //RenderHelper.enableGUIStandardItemLighting(); //TODO: Is there an replacement ?
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.pushMatrix();
        {
            RenderSystem.translatef(guiLeft, guiTop, 0.0F);
            drawWidgets(transform);
        }
        RenderSystem.popMatrix();

        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        window.draw(transform, mouseY, mouseX);

        bindTexture(textureFile);
    }

    protected void drawBackground(MatrixStack transform) {
        bindTexture(textureFile);

        //int x = (width - xSize) / 2;
        //int y = (height - ySize) / 2;
        blit(transform, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected void drawWidgets(MatrixStack transform) {
        ledgerManager.drawLedgers(transform);
        widgetManager.drawWidgets(transform);
    }

    protected void bindTexture(ResourceLocation texturePath) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(texturePath);
    }

    @Override
    public int getSizeX() {
        return xSize;
    }

    @Override
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
    public Minecraft getMC() {
        return minecraft;
    }

    public List<Rectangle2d> getExtraGuiAreas() {
        return ledgerManager.getLedgerAreas();
    }

    public TextLayoutHelper getTextLayout() {
        return textLayout;
    }
}
