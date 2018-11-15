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
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
import forestry.core.utils.Translator;
import forestry.mail.inventory.ItemInventoryLetter;
import forestry.mail.network.packets.PacketLetterInfoRequest;

import org.lwjgl.input.Keyboard;

public class GuiLetter extends GuiForestry<ContainerLetter> {
	private final ItemInventoryLetter itemInventory;
	private final boolean isProcessedLetter;
	private boolean checkedSessionVars;

	private GuiTextField address;
	private GuiTextBox text;

	private boolean addressFocus;
	private boolean textFocus;

	private final ArrayList<Widget> tradeInfoWidgets;

	public GuiLetter(EntityPlayer player, ItemInventoryLetter itemInventory) {
		super(Constants.TEXTURE_PATH_GUI + "/letter.png", new ContainerLetter(player, itemInventory));

		this.itemInventory = itemInventory;
		this.xSize = 194;
		this.ySize = 227;

		this.isProcessedLetter = container.getLetter().isProcessed();
		this.widgetManager.add(new AddresseeSlot(widgetManager, 16, 12, container));
		this.tradeInfoWidgets = new ArrayList<>();

		address = new GuiTextField(0, this.fontRenderer, guiLeft + 46, guiTop + 13, 93, 13);
		text = new GuiTextBox(1, this.fontRenderer, guiLeft + 17, guiTop + 31, 122, 57);
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);

		address = new GuiTextField(0, this.fontRenderer, guiLeft + 46, guiTop + 13, 93, 13);
		IMailAddress recipient = container.getRecipient();
		if (recipient != null) {
			address.setText(recipient.getName());
		}

		text = new GuiTextBox(1, this.fontRenderer, guiLeft + 17, guiTop + 31, 122, 57);
		text.setMaxStringLength(128);
		if (!container.getText().isEmpty()) {
			text.setText(container.getText());
		}
	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) throws IOException {

		// Set focus or enter text into address
		if (this.address.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				this.address.setFocused(false);
			} else {
				this.address.textboxKeyTyped(eventCharacter, eventKey);
			}
			return;
		}

		if (this.text.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				if (GuiScreen.isShiftKeyDown()) {
					text.setText(text.getText() + "\n");
				} else {
					this.text.setFocused(false);
				}
			} else if (eventKey == Keyboard.KEY_DOWN) {
				text.advanceLine();
			} else if (eventKey == Keyboard.KEY_UP) {
				text.regressLine();
			} else if (text.moreLinesAllowed() || eventKey == Keyboard.KEY_DELETE || eventKey == Keyboard.KEY_BACK) {
				this.text.textboxKeyTyped(eventCharacter, eventKey);
			}
			return;
		}

		super.keyTyped(eventCharacter, eventKey);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		this.address.mouseClicked(mouseX, mouseY, mouseButton);
		this.text.mouseClicked(mouseX, mouseY, mouseButton);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {

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

		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		if (this.isProcessedLetter) {
			fontRenderer.drawString(address.getText(), guiLeft + 49, guiTop + 16, ColourProperties.INSTANCE.get("gui.mail.lettertext"));
			fontRenderer.drawSplitString(text.getText(), guiLeft + 20, guiTop + 34, 119, ColourProperties.INSTANCE.get("gui.mail.lettertext"));
		} else {
			clearTradeInfoWidgets();
			address.drawTextBox();
			if (container.getCarrierType() == EnumAddressee.TRADER) {
				drawTradePreview(18, 32);
			} else {
				text.drawTextBox();
			}
		}
	}

	private void drawTradePreview(int x, int y) {

		String infoString = null;
		if (container.getTradeInfo() == null) {
			infoString = Translator.translateToLocal("for.gui.mail.no.trader");
		} else if (container.getTradeInfo().getTradegood().isEmpty()) {
			infoString = Translator.translateToLocal("for.gui.mail.nothing.to.trade");
		} else if (!container.getTradeInfo().getState().isOk()) {
			infoString = container.getTradeInfo().getState().getDescription();
		}

		if (infoString != null) {
			fontRenderer.drawSplitString(infoString, guiLeft + x, guiTop + y, 119, ColourProperties.INSTANCE.get("gui.mail.lettertext"));
			return;
		}

		fontRenderer.drawString(Translator.translateToLocal("for.gui.mail.pleasesend"), guiLeft + x, guiTop + y, ColourProperties.INSTANCE.get("gui.mail.lettertext"));

		addTradeInfoWidget(new ItemStackWidget(widgetManager, x, y + 10, container.getTradeInfo().getTradegood()));

		fontRenderer.drawString(Translator.translateToLocal("for.gui.mail.foreveryattached"), guiLeft + x, guiTop + y + 28, ColourProperties.INSTANCE.get("gui.mail.lettertext"));

		for (int i = 0; i < container.getTradeInfo().getRequired().size(); i++) {
			addTradeInfoWidget(new ItemStackWidget(widgetManager, x + i * 18, y + 38, container.getTradeInfo().getRequired().get(i)));
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
	public void onGuiClosed() {
		String recipientName = this.address.getText();
		EnumAddressee recipientType = container.getCarrierType();
		setRecipient(recipientName, recipientType);
		setText();
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
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

	@SideOnly(Side.CLIENT)
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
