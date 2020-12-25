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
package forestry.mail.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.IMailAddress;
import forestry.core.config.Constants;
import forestry.core.config.SessionVars;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiTextBox;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.mail.inventory.ItemInventoryLetter;
import forestry.mail.network.packets.PacketLetterInfoRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class GuiLetter extends GuiForestry<ContainerLetter> {
    private final ItemInventoryLetter itemInventory;
    private final boolean isProcessedLetter;
    private final ArrayList<Widget> tradeInfoWidgets;
    private boolean checkedSessionVars;
    private TextFieldWidget address;
    private GuiTextBox text;
    private boolean addressFocus;
    private boolean textFocus;

    public GuiLetter(ContainerLetter container, PlayerInventory inv, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "letter.png", container, inv, title);
        this.minecraft = Minecraft.getInstance(); //not 100% why this is needed, maybe side issues

        this.itemInventory = container.getItemInventory();
        this.xSize = 194;
        this.ySize = 227;

        this.isProcessedLetter = container.getLetter().isProcessed();
        this.widgetManager.add(new AddresseeSlot(widgetManager, 16, 12, container));
        this.tradeInfoWidgets = new ArrayList<>();
        address = new TextFieldWidget(this.minecraft.fontRenderer, guiLeft + 46, guiTop + 13, 93, 13, null);
        text = new GuiTextBox(this.minecraft.fontRenderer, guiLeft + 17, guiTop + 31, 122, 57);
    }

    @Override
    public void init() {
        super.init();

        minecraft.keyboardListener.enableRepeatEvents(true);

        address = new TextFieldWidget(this.minecraft.fontRenderer, guiLeft + 46, guiTop + 13, 93, 13, null);
        address.setEnabled(true);
        IMailAddress recipient = container.getRecipient();
        if (recipient != null) {
            address.setText(recipient.getName());
        }

        text = new GuiTextBox(this.minecraft.fontRenderer, guiLeft + 17, guiTop + 31, 122, 57);
        text.setEnabled(true);
        text.setMaxStringLength(128);
        if (!container.getText().isEmpty()) {
            text.setText(container.getText());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Set focus or enter text into address
        if (this.address.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                this.address.setFocused2(false);
            } else {
                this.address.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        if (this.text.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                if (hasShiftDown()) {
                    text.setText(text.getText() + "\n");
                } else {
                    this.text.setFocused2(false);
                }
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                text.advanceLine();
            } else if (keyCode == GLFW.GLFW_KEY_UP) {
                text.regressLine();
            } else if (
                    text.moreLinesAllowed() ||
                    keyCode == GLFW.GLFW_KEY_DELETE ||
                    keyCode == GLFW.GLFW_KEY_BACKSLASH
            ) {
                this.text.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Set focus or enter text into address
        if (this.address.isFocused()) {
            this.address.charTyped(codePoint, modifiers);
            return true;
        }

        if (this.text.isFocused()) {
            this.text.charTyped(codePoint, modifiers);
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.address.mouseClicked(mouseX, mouseY, mouseButton);
        this.text.mouseClicked(mouseX, mouseY, mouseButton);

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        if (!isProcessedLetter && !checkedSessionVars) {
            checkedSessionVars = true;
            setFromSessionVars();
            String recipient = this.address.getText();
            EnumAddressee recipientType = container.getCarrierType();
            setRecipient(recipient, recipientType);
        }

        // Check for focus changes
        if (addressFocus != address.isFocused()) {
            String recipient = this.address.getText();
            if (StringUtils.isNotBlank(recipient)) {
                EnumAddressee recipientType = container.getCarrierType();
                setRecipient(recipient, recipientType);
            }
        }
        addressFocus = address.isFocused();
        if (textFocus != text.isFocused()) {
            setText();
        }
        textFocus = text.isFocused();

        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        if (this.isProcessedLetter) {
            minecraft.fontRenderer.drawString(
                    transform,
                    address.getText(),
                    guiLeft + 49,
                    guiTop + 16,
                    ColourProperties.INSTANCE.get("gui.mail.lettertext")
            );
            minecraft.fontRenderer.func_238418_a_(
                    new StringTextComponent(text.getText()),
                    guiLeft + 20,
                    guiTop + 34,
                    119,
                    ColourProperties.INSTANCE.get("gui.mail.lettertext")
            );
        } else {
            clearTradeInfoWidgets();
            address.render(transform, mouseX, mouseY, partialTicks);
            if (container.getCarrierType() == EnumAddressee.TRADER) {
                drawTradePreview(transform, 18, 32);
            } else {
                text.render(transform, mouseX, mouseY, partialTicks);
            }
        }
    }

    private void drawTradePreview(MatrixStack transform, int x, int y) {
        ITextComponent infoString = null;
        if (container.getTradeInfo() == null) {
            infoString = new TranslationTextComponent("for.gui.mail.no.trader");
        } else if (container.getTradeInfo().getTradegood().isEmpty()) {
            infoString = new TranslationTextComponent("for.gui.mail.nothing.to.trade");
        } else if (!container.getTradeInfo().getState().isOk()) {
            infoString = container.getTradeInfo().getState().getDescription();
        }

        if (infoString != null) {
            minecraft.fontRenderer.func_238418_a_(
                    infoString,
                    guiLeft + x,
                    guiTop + y,
                    119,
                    ColourProperties.INSTANCE.get("gui.mail.lettertext")
            );
            return;
        }

        minecraft.fontRenderer.func_243248_b(
                transform,
                new TranslationTextComponent("for.gui.mail.pleasesend"),
                guiLeft + x,
                guiTop + y,
                ColourProperties.INSTANCE.get("gui.mail.lettertext")
        );

        addTradeInfoWidget(new ItemStackWidget(widgetManager, x, y + 10, container.getTradeInfo().getTradegood()));

        minecraft.fontRenderer.func_243248_b(
                transform,
                new TranslationTextComponent("for.gui.mail.foreveryattached"),
                guiLeft + x,
                guiTop + y + 28,
                ColourProperties.INSTANCE.get("gui.mail.lettertext")
        );

        for (int i = 0; i < container.getTradeInfo().getRequired().size(); i++) {
            addTradeInfoWidget(new ItemStackWidget(
                    widgetManager,
                    x + i * 18,
                    y + 38,
                    container.getTradeInfo().getRequired().get(i)
            ));
        }
    }

    private void addTradeInfoWidget(Widget widget) {
        tradeInfoWidgets.add(widget);
        widgetManager.add(widget);
    }

    private void clearTradeInfoWidgets() {
        for (Widget widget : tradeInfoWidgets) {
            widgetManager.remove(widget);
        }
        tradeInfoWidgets.clear();
    }

    @Override
    public void onClose() {
        String recipientName = this.address.getText();
        EnumAddressee recipientType = container.getCarrierType();
        setRecipient(recipientName, recipientType);
        setText();
        minecraft.keyboardListener.enableRepeatEvents(false);
        super.onClose();
    }

    private void setFromSessionVars() {
        if (SessionVars.getStringVar("mail.letter.recipient") == null) {
            return;
        }

        String recipient = SessionVars.getStringVar("mail.letter.recipient");
        String typeName = SessionVars.getStringVar("mail.letter.addressee");

        if (StringUtils.isNotBlank(recipient) && StringUtils.isNotBlank(typeName)) {
            address.setText(recipient);

            EnumAddressee type = EnumAddressee.fromString(typeName);
            container.setCarrierType(type);
        }

        SessionVars.clearStringVar("mail.letter.recipient");
        SessionVars.clearStringVar("mail.letter.addressee");
    }

    private void setRecipient(String recipientName, EnumAddressee type) {
        if (this.isProcessedLetter || StringUtils.isBlank(recipientName)) {
            return;
        }

        PacketLetterInfoRequest packet = new PacketLetterInfoRequest(recipientName, type);
        NetworkUtil.sendToServer(packet);
    }

    @OnlyIn(Dist.CLIENT)
    private void setText() {
        if (this.isProcessedLetter) {
            return;
        }

        container.setText(this.text.getText());
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(itemInventory);
        addHintLedger("letter");
    }
}
