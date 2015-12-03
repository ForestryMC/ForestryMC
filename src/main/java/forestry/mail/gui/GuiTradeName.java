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

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.mail.network.packets.PacketTraderAddressRequest;
import forestry.mail.tiles.TileTrader;

public class GuiTradeName extends GuiForestry<ContainerTradeName, TileTrader> {

	private GuiTextField addressNameField;

	public GuiTradeName(TileTrader tile) {
		super(Constants.TEXTURE_PATH_GUI + "/tradername.png", new ContainerTradeName(tile), tile);
		this.xSize = 176;
		this.ySize = 90;
	}

	@Override
	public void initGui() {
		super.initGui();

		addressNameField = new GuiTextField(this.fontRendererObj, guiLeft + 44, guiTop + 39, 90, 14);
		if (container.getAddress() != null) {
			addressNameField.setText(container.getAddress().getName());
		}
		addressNameField.setFocused(true);
	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) {

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
	protected void mouseClicked(int par1, int par2, int mouseButton) {
		super.mouseClicked(par1, par2, mouseButton);
		addressNameField.mouseClicked(par1, par2, mouseButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);

		String prompt = StringUtil.localize("gui.mail.nametrader");
		textLayout.startPage();
		textLayout.newLine();
		textLayout.drawCenteredLine(prompt, 0, fontColor.get("gui.mail.text"));
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
			PacketTraderAddressRequest packet = new PacketTraderAddressRequest(inventory, address);
			Proxies.net.sendToServer(packet);
		}
	}

}
