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
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ITradeStationInfo;
import forestry.core.config.SessionVars;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.utils.Translator;

public class GuiCatalogue extends GuiForestry<ContainerCatalogue, IInventory> {

	private static final String boldUnderline = TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE;

	private GuiButton buttonFilter;
	private GuiButton buttonUse;

	private final List<ItemStackWidget> tradeInfoWidgets = new ArrayList<>();

	public GuiCatalogue(EntityPlayer player) {
		super(new ResourceLocation("textures/gui/book.png"), new ContainerCatalogue(player), null);
		this.xSize = 192;
		this.ySize = 192;
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.clear();
		Keyboard.enableRepeatEvents(true);

		buttonList.add(new GuiButton(2, width / 2 + 44, guiTop + 150, 12, 20, ">"));
		buttonList.add(new GuiButton(3, width / 2 - 58, guiTop + 150, 12, 20, "<"));

		buttonFilter = new GuiButton(4, width / 2 - 44, guiTop + 150, 42, 20, Translator.translateToLocal("for.gui.mail.filter.all"));
		buttonList.add(buttonFilter);

		buttonUse = new GuiButton(5, width / 2, guiTop + 150, 42, 20, Translator.translateToLocal("for.gui.mail.address.copy"));
		buttonList.add(buttonUse);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		fontRendererObj.drawString(String.format("%s / %s", container.getPageNumber(), container.getPageCount()), guiLeft + xSize - 72, guiTop + 12, fontColor.get("gui.book"));

		clearTradeInfoWidgets();

		ITradeStationInfo tradeInfo = container.getTradeInfo();

		if (tradeInfo != null) {
			drawTradePreview(tradeInfo, guiLeft + 38, guiTop + 30);
			buttonUse.enabled = tradeInfo.getState().isOk();
		} else {
			drawNoTrade(guiLeft + 38, guiTop + 30);
			buttonUse.enabled = false;
		}

		buttonFilter.displayString = Translator.translateToLocal("for.gui.mail.filter." + container.getFilterIdent());
	}

	private void drawNoTrade(int x, int y) {
		fontRendererObj.drawSplitString(Translator.translateToLocal("for.gui.mail.notrades"), x, y + 18, 119, fontColor.get("gui.book"));
	}

	private void drawTradePreview(ITradeStationInfo tradeInfo, int x, int y) {

		fontRendererObj.drawString(boldUnderline + tradeInfo.getAddress().getName(), x, y, fontColor.get("gui.book"));

		fontRendererObj.drawString(String.format(Translator.translateToLocal("for.gui.mail.willtrade"), tradeInfo.getOwner().getName()), x, y + 18, fontColor.get("gui.book"));

		addTradeInfoWidget(new ItemStackWidget(widgetManager, x - guiLeft, y - guiTop + 28, tradeInfo.getTradegood()));

		fontRendererObj.drawString(Translator.translateToLocal("for.gui.mail.tradefor"), x, y + 46, fontColor.get("gui.book"));

		for (int i = 0; i < tradeInfo.getRequired().length; i++) {
			ItemStack itemStack = tradeInfo.getRequired()[i];
			addTradeInfoWidget(new ItemStackWidget(widgetManager, x - guiLeft + i * 18, y - guiTop + 56, itemStack));
		}

		if (tradeInfo.getState().isOk()) {
			fontRendererObj.drawSplitString(TextFormatting.DARK_GREEN + tradeInfo.getState().getDescription(), x, y + 82, 119, fontColor.get("gui.book"));
		} else {
			fontRendererObj.drawSplitString(TextFormatting.DARK_RED + tradeInfo.getState().getDescription(), x, y + 82, 119, fontColor.get("gui.book"));
		}
	}

	private void addTradeInfoWidget(ItemStackWidget widget) {
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
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 0:
				mc.thePlayer.closeScreen();
				break;
			case 2:
				container.nextPage();
				break;
			case 3:
				container.previousPage();
				break;
			case 4:
				container.cycleFilter();
				break;
			case 5:
				ITradeStationInfo info = container.getTradeInfo();
				if (info != null) {
					SessionVars.setStringVar("mail.letter.recipient", info.getAddress().getName());
					SessionVars.setStringVar("mail.letter.addressee", EnumAddressee.TRADER.toString());
				}
				mc.thePlayer.closeScreen();
				break;
		}
	}
}
