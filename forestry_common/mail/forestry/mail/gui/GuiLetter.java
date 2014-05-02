/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import java.util.Locale;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.MailAddress;
import forestry.api.mail.PostManager;
import forestry.core.config.Defaults;
import forestry.core.config.SessionVars;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiTextBox;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.mail.EnumAddressee;
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
			return StringUtil.localize("gui.addressee." + container.getCarrierType().toLowerCase(Locale.ENGLISH));
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

	private final ContainerLetter container;

	public GuiLetter(EntityPlayer player, LetterInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/letter.png", new ContainerLetter(player, inventory), inventory);
		this.xSize = 194;
		this.ySize = 227;

		this.container = (ContainerLetter) inventorySlots;
		this.isProcessedLetter = container.getLetter().isProcessed();
		this.widgetManager.add(new AddresseeSlot(16, 12));
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);

		address = new GuiTextField(this.fontRendererObj, guiLeft + 46, guiTop + 13, 93, 13);
		if (container.getRecipient() != null) {
			address.setText(container.getRecipient().getIdentifier());
			this.setRecipient(container.getRecipient().getIdentifier(), container.getCarrierType());
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
		if (addressFocus != address.isFocused())
			setRecipient(this.address.getText(), container.getCarrierType());
		addressFocus = address.isFocused();
		if (textFocus != text.isFocused())
			setText();
		textFocus = text.isFocused();

		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		if (this.isProcessedLetter) {
			fontRendererObj.drawString(address.getText(), guiLeft + 49, guiTop + 16, fontColor.get("gui.mail.lettertext"));
			fontRendererObj.drawSplitString(text.getText(), guiLeft + 20, guiTop + 34, 119, fontColor.get("gui.mail.lettertext"));
		} else {
			address.drawTextBox();
			if (container.getCarrierType().equals(EnumAddressee.TRADER.toString().toLowerCase(Locale.ENGLISH)))
				drawTradePreview(guiLeft + 18, guiTop + 32);
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
			fontRendererObj.drawSplitString(StringUtil.localize(infoString), x, y, 119, fontColor.get("gui.mail.lettertext"));
			return;
		}

		fontRendererObj.drawString(StringUtil.localize("gui.mail.pleasesend"), x, y, fontColor.get("gui.mail.lettertext"));

		itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, container.getTradeInfo().tradegood, x, y + 10);
		itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, container.getTradeInfo().tradegood, x, y + 10);

		GL11.glDisable(GL11.GL_LIGHTING);
		fontRendererObj.drawString(StringUtil.localize("gui.mail.foreveryattached"), x, y + 28, fontColor.get("gui.mail.lettertext"));
		GL11.glEnable(GL11.GL_LIGHTING);
		for (int i = 0; i < container.getTradeInfo().required.length; i++) {
			GL11.glDisable(GL11.GL_LIGHTING);
			itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, container.getTradeInfo().required[i], x + i * 18, y + 38);
			itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, container.getTradeInfo().required[i], x + i * 18, y + 38);
			GL11.glEnable(GL11.GL_LIGHTING);
		}

	}

	@Override
	public void onGuiClosed() {
		setRecipient(this.address.getText(), container.getCarrierType());
		setText();
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	private void setFromSessionVars() {
		if(SessionVars.getStringVar("mail.letter.recipient") == null)
			return;

		String recipient = SessionVars.getStringVar("mail.letter.recipient");
		String type = SessionVars.getStringVar("mail.letter.addressee");
		if(type != null) {
			address.setText(recipient);
			setRecipient(recipient, type);
		}

		SessionVars.clearStringVar("mail.letter.recipient");
		SessionVars.clearStringVar("mail.letter.addressee");
	}

	private void setRecipient(String identifier, String type) {
		if (this.isProcessedLetter)
			return;

		MailAddress recipient = new MailAddress(identifier, type);
		container.setRecipient(recipient);
		container.updateTradeInfo(this.mc.theWorld, recipient);
	}

	private void setText() {
		if (this.isProcessedLetter)
			return;

		container.setText(this.text.getText());
	}
}
