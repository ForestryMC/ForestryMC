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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.GuiTextField;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import forestry.mail.network.packets.PacketTraderAddressRequest;
import forestry.mail.tiles.TileTrader;

import org.lwjgl.input.Keyboard;

public class GuiTradeName extends GuiForestry<ContainerTradeName> {
	private final TileTrader tile;
	private GuiTextField addressNameField;

	public GuiTradeName(TileTrader tile) {
		super(Constants.TEXTURE_PATH_GUI + "/tradername.png", new ContainerTradeName(tile));
		this.tile = tile;
		this.xSize = 176;
		this.ySize = 90;

		addressNameField = new GuiTextField(0, this.fontRenderer, guiLeft + 44, guiTop + 39, 90, 14);
	}

	@Override
	public void initGui() {
		super.initGui();

		addressNameField = new GuiTextField(0, this.fontRenderer, guiLeft + 44, guiTop + 39, 90, 14);
		addressNameField.setText(container.getAddress().getName());
		addressNameField.setFocused(true);
	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) throws IOException {

		// Set focus or enter text into address
		if (addressNameField.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				setAddress();
			} else {
				addressNameField.textboxKeyTyped(eventCharacter, eventKey);
			}
			return;
		}

		super.keyTyped(eventCharacter, eventKey);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		addressNameField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);

		String prompt = Translator.translateToLocal("for.gui.mail.nametrader");
		textLayout.startPage();
		textLayout.newLine();
		textLayout.drawCenteredLine(prompt, 0, ColourProperties.INSTANCE.get("gui.mail.text"));
		textLayout.endPage();
		addressNameField.drawTextBox();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
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
