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

import java.util.ArrayList;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.config.Defaults;
import forestry.core.config.SessionVars;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiTextBox;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.api.mail.EnumAddressee;
import forestry.mail.items.ItemLetter.LetterInventory;

public class GuiLetter extends GuiForestry<TileForestry> {

	protected class AddresseeSlot extends Widget {

		public AddresseeSlot(int xPos, int yPos) {
			super(widgetManager, xPos, yPos);
			this.width = 26;
			this.height = 15;
		}

		@Override
		public void draw(int startX, int startY) {

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			//mc.renderEngine.bindTexture(Defaults.TEXTURE_PATH_GUI + "/letter.png");
			IPostalCarrier carrier = PostManager.postRegistry.getCarrier(container.getCarrierType());
			if(carrier != null)
				drawTexturedModelRectFromIcon(startX + xPos, startY + yPos - 5, carrier.getIcon(), 26, 26);

		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			return StringUtil.localize("gui.addressee." + container.getCarrierType().toString());
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			if(!isProcessedLetter)
				container.advanceCarrierType();
		}

	}

	private final boolean isProcessedLetter;
	private boolean checkedSessionVars;

	private GuiTextField address;
	private GuiTextBox text;

	boolean addressFocus;
	boolean textFocus;

	protected final ArrayList<Widget> tradeInfoWidgets;

	private final ContainerLetter container;

	public GuiLetter(EntityPlayer player, LetterInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/letter.png", new ContainerLetter(player, inventory), inventory);
		this.xSize = 194;
		this.ySize = 227;

		this.container = (ContainerLetter) inventorySlots;
		this.isProcessedLetter = container.getLetter().isProcessed();
		this.widgetManager.add(new AddresseeSlot(16, 12));
		this.tradeInfoWidgets = new ArrayList<Widget>();
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);

		address = new GuiTextField(this.fontRendererObj, guiLeft + 46, guiTop + 13, 93, 13);
		IMailAddress recipient = container.getRecipient();
		if (recipient != null) {
			address.setText(recipient.getName());
		}

		text = new GuiTextBox(this.fontRendererObj, guiLeft + 17, guiTop + 31, 122, 57);
		text.setMaxStringLength(128);
		if (!container.getText().isEmpty())
			text.setText(container.getText());
	}

	@Override
	protected void keyTyped(char eventCharacter, int eventKey) {

		// Set focus or enter text into address
		if (this.address.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN)
				this.address.setFocused(false);
			else
				this.address.textboxKeyTyped(eventCharacter, eventKey);
			return;
		}

		if (this.text.isFocused()) {
			if (eventKey == Keyboard.KEY_RETURN) {
				if(Proxies.common.isShiftDown())
					text.setText(text.getText() + "\n");
				else
					this.text.setFocused(false);
			} else if(eventKey == Keyboard.KEY_DOWN) {
				text.advanceLine();
			} else if(eventKey == Keyboard.KEY_UP) {
				text.regressLine();
			} else if(text.moreLinesAllowed() || eventKey == Keyboard.KEY_DELETE || eventKey == Keyboard.KEY_BACK)
				this.text.textboxKeyTyped(eventCharacter, eventKey);
			return;
		}

		super.keyTyped(eventCharacter, eventKey);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int mouseButton) {
		super.mouseClicked(par1, par2, mouseButton);

		this.address.mouseClicked(par1, par2, mouseButton);
		this.text.mouseClicked(par1, par2, mouseButton);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {

		if(!isProcessedLetter && !checkedSessionVars) {
			checkedSessionVars = true;
			setFromSessionVars();
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
		if (textFocus != text.isFocused())
			setText();
		textFocus = text.isFocused();

		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		if (this.isProcessedLetter) {
			fontRendererObj.drawString(address.getText(), guiLeft + 49, guiTop + 16, fontColor.get("gui.mail.lettertext"));
			fontRendererObj.drawSplitString(text.getText(), guiLeft + 20, guiTop + 34, 119, fontColor.get("gui.mail.lettertext"));
		} else {
			clearTradeInfoWidgets();
			address.drawTextBox();
			if (container.getCarrierType() == EnumAddressee.TRADER)
				drawTradePreview(18, 32);
			else
				text.drawTextBox();
		}
	}

	private void drawTradePreview(int x, int y) {

		String infoString = null;
		if (container.getTradeInfo() == null)
			infoString = "gui.mail.notrader";
		else if (container.getTradeInfo().tradegood == null)
			infoString = "gui.mail.nothingtotrade";
		else if (!container.getTradeInfo().state.isOk())
			infoString = "chat.mail." + container.getTradeInfo().state.getIdentifier();

		if (infoString != null) {
			fontRendererObj.drawSplitString(StringUtil.localize(infoString), guiLeft + x, guiTop + y, 119, fontColor.get("gui.mail.lettertext"));
			return;
		}

		fontRendererObj.drawString(StringUtil.localize("gui.mail.pleasesend"), guiLeft + x, guiTop + y, fontColor.get("gui.mail.lettertext"));

		addTradeInfoWidget(new ItemStackWidget(x, y + 10, container.getTradeInfo().tradegood));

		GL11.glDisable(GL11.GL_LIGHTING);
		fontRendererObj.drawString(StringUtil.localize("gui.mail.foreveryattached"), guiLeft + x, guiTop + y + 28, fontColor.get("gui.mail.lettertext"));
		GL11.glEnable(GL11.GL_LIGHTING);
		for (int i = 0; i < container.getTradeInfo().required.length; i++) {
			addTradeInfoWidget(new ItemStackWidget(x + i * 18, y + 38, container.getTradeInfo().required[i]));
		}
	}

	private void addTradeInfoWidget(Widget widget) {
		tradeInfoWidgets.add(widget);
		widgetManager.add(widget);
	}

	private void clearTradeInfoWidgets() {
		for (Widget widget : tradeInfoWidgets)
			widgetManager.remove(widget);
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
	
	@Override
	protected boolean checkHotbarKeys(int key) {
		return false;
	}

	private void setFromSessionVars() {
		if(SessionVars.getStringVar("mail.letter.recipient") == null)
			return;

		String recipient = SessionVars.getStringVar("mail.letter.recipient");
		String typeName = SessionVars.getStringVar("mail.letter.addressee");
		
		if (StringUtils.isNotBlank(recipient) && StringUtils.isNotBlank(typeName)) {
			address.setText(recipient);

			EnumAddressee type = EnumAddressee.fromString(typeName);
			setRecipient(recipient, type);
		}

		SessionVars.clearStringVar("mail.letter.recipient");
		SessionVars.clearStringVar("mail.letter.addressee");
	}

	private void setRecipient(String recipientName, EnumAddressee type) {
		if (this.isProcessedLetter || StringUtils.isBlank(recipientName) || type == null)
			return;

		container.setRecipient(recipientName, type);
	}

	private void setText() {
		if (this.isProcessedLetter)
			return;

		container.setText(this.text.getText());
	}
}
