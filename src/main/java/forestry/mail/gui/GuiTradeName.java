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
package forestry.mail.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.mail.network.packets.PacketTraderAddressRequest;
import forestry.mail.tiles.TileTrader;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

public class GuiTradeName extends GuiForestry<ContainerTradeName> {
    private final TileTrader tile;
    private TextFieldWidget addressNameField;

    public GuiTradeName(ContainerTradeName container, PlayerInventory inv, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "tradername.png", container, inv, title);
        this.tile = container.getTile();
        this.xSize = 176;
        this.ySize = 90;
    }

    @Override
    public void init() {
        super.init();

        addressNameField = new TextFieldWidget(this.minecraft.fontRenderer, guiLeft + 44, guiTop + 39, 90, 14, null);
        addressNameField.setText(container.getAddress().getName());
        addressNameField.setVisible(true);
        addressNameField.setFocused2(true);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Set focus or enter text into address
        if (addressNameField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                setAddress();
            } else {
                addressNameField.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Set focus or enter text into address
        if (addressNameField.isFocused()) {
            addressNameField.charTyped(codePoint, modifiers);
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        addressNameField.mouseClicked(mouseX, mouseY, mouseButton);
        return true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseX, mouseY);

        textLayout.startPage();
        textLayout.newLine();
        textLayout.drawCenteredLine(
                transform,
                new TranslationTextComponent("for.gui.mail.nametrader"),
                0,
                ColourProperties.INSTANCE.get("gui.mail.text")
        );
        textLayout.endPage();
        addressNameField.render(transform, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        setAddress();
    }

    private void setAddress() {
        String address = addressNameField.getText();
        if (StringUtils.isNotBlank(address)) {
            PacketTraderAddressRequest packet = new PacketTraderAddressRequest(tile, address);
            NetworkUtil.sendToServer(packet);
        }
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
    }
}
