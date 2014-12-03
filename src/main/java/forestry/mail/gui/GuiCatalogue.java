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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import forestry.api.mail.TradeStationInfo;
import forestry.core.config.SessionVars;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.api.mail.EnumAddressee;

public class GuiCatalogue extends GuiForestry<TileForestry> {

	GuiButton buttonTrade;
	GuiButton buttonClose;

	GuiButton buttonFilter;
	GuiButton buttonCopy;

	ContainerCatalogue container;

	public GuiCatalogue(EntityPlayer player) {
		super(new ResourceLocation("textures/gui/book.png"), new ContainerCatalogue(player));
		this.xSize = 192;
		this.ySize = 192;
		container = (ContainerCatalogue)inventorySlots;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();
		Keyboard.enableRepeatEvents(true);

		//buttonList.add(buttonTrade = new GuiButton(1, width / 2 - 100, 4 + ySize, 98, 20, StringUtil.localize("gui.mail.catalogue.trade")));
		//buttonList.add(buttonClose = new GuiButton(0, (width - 100) / 2, 4 + ySize, 98, 20, StringUtil.localize("gui.mail.catalogue.close")));

		buttonList.add(new GuiButton(2, width / 2 + 44, guiTop + 150, 12, 20, ">"));
		buttonList.add(new GuiButton(3, width / 2 - 58, guiTop + 150, 12, 20, "<"));

		buttonList.add(buttonFilter = new GuiButton(4, (width / 2) - 44, guiTop + 150, 42, 20, StringUtil.localize("gui.mail.filter.all")));

		buttonList.add(buttonCopy = new GuiButton(5, width / 2, guiTop + 150, 42, 20, StringUtil.localize("gui.mail.address.copy")));
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		fontRendererObj.drawString(String.format("%s / %s", container.getCurrentPos(), container.getMaxCount()), guiLeft + xSize - 72, guiTop + 12, fontColor.get("gui.book"));

		if(container.getTradeInfo() != null)
			drawTradePreview(guiLeft + 38, guiTop + 30);
		else
			drawNoTrade(guiLeft + 38, guiTop + 30);

		buttonFilter.displayString = StringUtil.localize("gui.mail.filter." + container.getFilterIdent());
		if(container.getTradeInfo() == null || !container.getTradeInfo().state.isOk())
			buttonCopy.enabled = false;
		else
			buttonCopy.enabled = true;
	}

	private void drawNoTrade(int x, int y) {
		fontRendererObj.drawSplitString(StringUtil.localize("gui.mail.notrades"), x, y + 18, 119, fontColor.get("gui.book"));
	}

	private void drawTradePreview(int x, int y) {

		fontRendererObj.drawString("\u00A7l\u00A7n" + container.getTradeInfo().address.getName(), x, y, fontColor.get("gui.book"));

		TradeStationInfo info = container.getTradeInfo();
		fontRendererObj.drawString(String.format(StringUtil.localize("gui.mail.willtrade"), info.owner.getName()), x, y + 18, fontColor.get("gui.book"));

		itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, info.tradegood, x, y + 28);
		itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, info.tradegood, x, y + 28);

		GL11.glDisable(GL11.GL_LIGHTING);
		fontRendererObj.drawString(StringUtil.localize("gui.mail.tradefor"), x, y + 46, fontColor.get("gui.book"));
		GL11.glEnable(GL11.GL_LIGHTING);
		for (int i = 0; i < container.getTradeInfo().required.length; i++) {
			GL11.glDisable(GL11.GL_LIGHTING);
			itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, info.required[i], x + i * 18, y + 56);
			itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, info.required[i], x + i * 18, y + 56);
			GL11.glEnable(GL11.GL_LIGHTING);
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		if(info.state.isOk())
			fontRendererObj.drawSplitString("\u00A72" + StringUtil.localize("chat.mail." + info.state.getIdentifier()), x, y + 82, 119, fontColor.get("gui.book"));
		else
			fontRendererObj.drawSplitString("\u00A74" + StringUtil.localize("chat.mail." + info.state.getIdentifier()), x, y + 82, 119, fontColor.get("gui.book"));
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch(button.id) {
		case 0:
			mc.displayGuiScreen(null);
			break;
		case 2:
			container.advanceIteration();
			break;
		case 3:
			container.regressIteration();
			break;
		case 4:
			container.cycleFilter();
			break;
		case 5:
			TradeStationInfo info = container.getTradeInfo();
			if(info != null) {
				SessionVars.setStringVar("mail.letter.recipient", info.address.getName());
				SessionVars.setStringVar("mail.letter.addressee", EnumAddressee.TRADER.toString());
			}
			mc.displayGuiScreen(null);
			break;
		}
	}
}
