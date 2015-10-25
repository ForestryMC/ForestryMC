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

import org.lwjgl.input.Keyboard;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.network.PacketTraderAddress;

public class GuiTradeName extends GuiForestry<ContainerTradeName, MachineTrader> {

	private GuiTextField addressNameField;

	public GuiTradeName(MachineTrader tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/tradername.png", new ContainerTradeName(tile), tile);
		this.xSize = 176;
		this.ySize = 90;
	}

	@Override
	public void initGui() {
		super.initGui();

		addressNameField = new GuiTextField(0, this.fontRendererObj, guiLeft + 44, guiTop + 39, 90, 14);
		if (container.getAddress() != null) {
			addressNameField.setText(container.getAddress().getName());
		}
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
	protected void mouseClicked(int par1, int par2, int mouseButton) throws IOException {
		super.mouseClicked(par1, par2, mouseButton);
		addressNameField.mouseClicked(par1, par2, mouseButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);

		String prompt = StringUtil.localize("gui.mail.nametrader");
		fontRendererObj.drawString(prompt, guiLeft + this.getCenteredOffset(prompt), guiTop + 16,
				fontColor.get("gui.mail.text"));
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
			PacketTraderAddress packet = new PacketTraderAddress(inventory, address);
			Proxies.net.sendToServer(packet);
		}
	}

}
