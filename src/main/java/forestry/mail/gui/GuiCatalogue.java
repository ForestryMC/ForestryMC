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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ITradeStationInfo;
import forestry.core.config.SessionVars;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

import org.lwjgl.input.Keyboard;

public class GuiCatalogue extends GuiForestry<ContainerCatalogue> {

	private static final String boldUnderline = TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE;

	private GuiButton buttonFilter;
	private GuiButton buttonUse;

	private final List<ItemStackWidget> tradeInfoWidgets = new ArrayList<>();

	public GuiCatalogue(EntityPlayer player) {
		super(new ResourceLocation("textures/gui/book.png"), new ContainerCatalogue(player));
		this.xSize = 192;
		this.ySize = 192;

		buttonFilter = new GuiButton(4, width / 2 - 44, guiTop + 150, 42, 20, Translator.translateToLocal("for.gui.mail.filter.all"));
		buttonUse = new GuiButton(5, width / 2, guiTop + 150, 42, 20, Translator.translateToLocal("for.gui.mail.address.copy"));
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

		fontRenderer.drawString(String.format("%s / %s", container.getPageNumber(), container.getPageCount()), guiLeft + xSize - 72, guiTop + 12, ColourProperties.INSTANCE.get("gui.book"));

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
		fontRenderer.drawSplitString(Translator.translateToLocal("for.gui.mail.notrades"), x, y + 18, 119, ColourProperties.INSTANCE.get("gui.book"));
	}

	private void drawTradePreview(ITradeStationInfo tradeInfo, int x, int y) {

		fontRenderer.drawString(boldUnderline + tradeInfo.getAddress().getName(), x, y, ColourProperties.INSTANCE.get("gui.book"));

		fontRenderer.drawString(String.format(Translator.translateToLocal("for.gui.mail.willtrade"), tradeInfo.getOwner().getName()), x, y + 18, ColourProperties.INSTANCE.get("gui.book"));

		addTradeInfoWidget(new ItemStackWidget(widgetManager, x - guiLeft, y - guiTop + 28, tradeInfo.getTradegood()));

		fontRenderer.drawString(Translator.translateToLocal("for.gui.mail.tradefor"), x, y + 46, ColourProperties.INSTANCE.get("gui.book"));

		for (int i = 0; i < tradeInfo.getRequired().size(); i++) {
			ItemStack itemStack = tradeInfo.getRequired().get(i);
			addTradeInfoWidget(new ItemStackWidget(widgetManager, x - guiLeft + i * 18, y - guiTop + 56, itemStack));
		}

		if (tradeInfo.getState().isOk()) {
			fontRenderer.drawSplitString(TextFormatting.DARK_GREEN + tradeInfo.getState().getDescription(), x, y + 82, 119, ColourProperties.INSTANCE.get("gui.book"));
		} else {
			fontRenderer.drawSplitString(TextFormatting.DARK_RED + tradeInfo.getState().getDescription(), x, y + 82, 119, ColourProperties.INSTANCE.get("gui.book"));
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
				mc.player.closeScreen();
				break;
			case 2: // next page
				NetworkUtil.sendToServer(new PacketGuiSelectRequest(0, 0));
				break;
			case 3: // previous page
				NetworkUtil.sendToServer(new PacketGuiSelectRequest(1, 0));
				break;
			case 4: // cycle filter
				NetworkUtil.sendToServer(new PacketGuiSelectRequest(2, 0));
				break;
			case 5:
				ITradeStationInfo info = container.getTradeInfo();
				if (info != null) {
					SessionVars.setStringVar("mail.letter.recipient", info.getAddress().getName());
					SessionVars.setStringVar("mail.letter.addressee", EnumAddressee.TRADER.toString());
				}
				mc.player.closeScreen();
				break;
		}
	}

	@Override
	protected void addLedgers() {

	}
}
