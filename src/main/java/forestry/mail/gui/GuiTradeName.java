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

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.mail.gadgets.MachineTrader;

public class GuiTradeName extends GuiForestry<MachineTrader> {

	private GuiTextField addressNameField;

	private boolean addressNameFocus;

	private final ContainerTradeName container;

	public GuiTradeName(InventoryPlayer inventoryplayer, MachineTrader tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/tradername.png", new ContainerTradeName(inventoryplayer, tile), tile);
		this.xSize = 176;
		this.ySize = 90;

		this.container = (ContainerTradeName) inventorySlots;
	}

	@Override
	public void initGui() {
		super.initGui();

		addressNameField = new GuiTextField(this.fontRendererObj, guiLeft + 44, guiTop + 39, 90, 14);
		if (container.getAddress() != null)
			addressNameField.setText(container.getAddress().getName());
		addressNameField.setFocused(true);
	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) {

		// Set focus or enter text into address
		if (addressNameField.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				addressNameFocus = true;
				addressNameField.setFocused(false);
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
		// Close gui screen if we linked up.
		if (container.machine.isLinked()) {
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
		}

		// Check for focus changes
		if (addressNameFocus && !addressNameField.isFocused())
			this.setAddress();
		addressNameFocus = addressNameField.isFocused();

		super.drawGuiContainerBackgroundLayer(var1, var2, var3);

		String prompt = StringUtil.localize("gui.mail.nametrader");
		fontRendererObj.drawString(prompt, guiLeft + this.getCenteredOffset(prompt), guiTop + 16, fontColor.get("gui.mail.text"));
		addressNameField.drawTextBox();

	}

	@Override
	public void onGuiClosed() {
		setAddress();
		super.onGuiClosed();
	}

	private void setAddress() {
		String address = addressNameField.getText();
		if (StringUtils.isNotBlank(address))
			container.setAddress(address);
	}

}
