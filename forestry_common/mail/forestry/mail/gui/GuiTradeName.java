/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.input.Keyboard;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.mail.gadgets.MachineTrader;

public class GuiTradeName extends GuiForestry<MachineTrader> {

	private GuiTextField moniker;

	private boolean monikerFocus;

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

		moniker = new GuiTextField(this.fontRendererObj, guiLeft + 44, guiTop + 39, 90, 14);
		if (container.getMoniker() != null)
			moniker.setText(container.getMoniker());
	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) {

		// Set focus or enter text into address
		if (this.moniker.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN)
				this.moniker.setFocused(false);
			else
				this.moniker.textboxKeyTyped(eventCharacter, eventKey);
			return;
		}

		super.keyTyped(eventCharacter, eventKey);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int mouseButton) {
		super.mouseClicked(par1, par2, mouseButton);
		this.moniker.mouseClicked(par1, par2, mouseButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {

		// Close gui screen if we linked up.
		if (container.machine.isLinked()) {
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
		}

		// Check for focus changes
		if (monikerFocus != moniker.isFocused())
			setMoniker();
		monikerFocus = moniker.isFocused();

		super.drawGuiContainerBackgroundLayer(var1, var2, var3);

		String prompt = StringUtil.localize("gui.mail.nametrader");
		fontRendererObj.drawString(prompt, guiLeft + this.getCenteredOffset(prompt), guiTop + 16, fontColor.get("gui.mail.text"));
		moniker.drawTextBox();

	}

	@Override
	public void onGuiClosed() {
		setMoniker();
		super.onGuiClosed();
	}

	private void setMoniker() {
		container.setMoniker(this.moniker.getText());
	}

}
