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

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import com.mojang.blaze3d.vertex.PoseStack;

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

public class GuiCatalogue extends GuiForestry<ContainerCatalogue> {

	private static final String boldUnderline = ChatFormatting.BOLD.toString() + ChatFormatting.UNDERLINE;

	private Button buttonFilter;
	private Button buttonUse;

	private final List<ItemStackWidget> tradeInfoWidgets = new ArrayList<>();

	public GuiCatalogue(ContainerCatalogue container, Inventory inv, Component title) {
		super(new ResourceLocation("textures/gui/book.png"), container, inv, title);
		this.imageWidth = 192;
		this.imageHeight = 192;

		buttonFilter = new Button(width / 2 - 44, topPos + 150, 42, 20, Component.translatable("for.gui.mail.filter.all"), b -> actionPerformed(4));
		buttonUse = new Button(width / 2, topPos + 150, 42, 20, Component.translatable("for.gui.mail.address.copy"), b -> actionPerformed(5));
	}

	@Override
	public void init() {
		super.init();

		renderables.clear();

		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);

		addRenderableWidget(new Button(width / 2 + 44, topPos + 150, 12, 20, Component.literal(">"), b -> actionPerformed(2)));
		addRenderableWidget(new Button(width / 2 - 58, topPos + 150, 12, 20, Component.literal("<"), b -> actionPerformed(3)));

		//TODO but these are set in the constructor??
		buttonFilter = new Button(width / 2 - 44, topPos + 150, 42, 20, Component.translatable("for.gui.mail.filter.all"), b -> actionPerformed(4));
		addRenderableWidget(buttonFilter);

		buttonUse = new Button(width / 2, topPos + 150, 42, 20, Component.translatable("for.gui.mail.address.copy"), b -> actionPerformed(5));
		addRenderableWidget(buttonUse);
	}

	@Override
	public void removed() {
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
		super.removed();
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		Minecraft.getInstance().font.draw(transform, String.format("%s / %s", container.getPageNumber(), container.getPageCount()), leftPos + imageWidth - 72, topPos + 12, ColourProperties.INSTANCE.get("gui.book"));

		clearTradeInfoWidgets();

		ITradeStationInfo tradeInfo = container.getTradeInfo();

		if (tradeInfo != null) {
			drawTradePreview(transform, tradeInfo, leftPos + 38, topPos + 30);
			buttonUse.visible = tradeInfo.getState().isOk();
		} else {
			drawNoTrade(leftPos + 38, topPos + 30);
			buttonUse.visible = false;
		}

		buttonFilter.setMessage(Component.translatable("for.gui.mail.filter." + container.getFilterIdent()));
	}

	private void drawNoTrade(int x, int y) {
		Minecraft.getInstance().font.drawWordWrap(Component.translatable("for.gui.mail.notrades"), x, y + 18, 119, ColourProperties.INSTANCE.get("gui.book"));
	}

	private void drawTradePreview(PoseStack transform, ITradeStationInfo tradeInfo, int x, int y) {

		Font fontRenderer = Minecraft.getInstance().font;
		fontRenderer.draw(transform, boldUnderline + tradeInfo.getAddress().getName(), x, y, ColourProperties.INSTANCE.get("gui.book"));

		fontRenderer.draw(transform, String.format(Translator.translateToLocal("for.gui.mail.willtrade"), tradeInfo.getOwner().getName()), x, y + 18, ColourProperties.INSTANCE.get("gui.book"));

		addTradeInfoWidget(new ItemStackWidget(widgetManager, x - leftPos, y - topPos + 28, tradeInfo.getTradegood()));

		fontRenderer.draw(transform, Translator.translateToLocal("for.gui.mail.tradefor"), x, y + 46, ColourProperties.INSTANCE.get("gui.book"));

		for (int i = 0; i < tradeInfo.getRequired().size(); i++) {
			ItemStack itemStack = tradeInfo.getRequired().get(i);
			addTradeInfoWidget(new ItemStackWidget(widgetManager, x - leftPos + i * 18, y - topPos + 56, itemStack));
		}

		//TODO: Fix later
		if (tradeInfo.getState().isOk()) {
			fontRenderer.drawWordWrap(((MutableComponent) tradeInfo.getState().getDescription()).withStyle(ChatFormatting.DARK_GREEN), x, y + 82, 119, ColourProperties.INSTANCE.get("gui.book"));
		} else {
			fontRenderer.drawWordWrap(((MutableComponent) tradeInfo.getState().getDescription()).withStyle(ChatFormatting.DARK_RED), x, y + 82, 119, ColourProperties.INSTANCE.get("gui.book"));
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

	protected void actionPerformed(int id) {
		LocalPlayer player = Minecraft.getInstance().player;
		switch (id) {
			case 0 -> player.closeContainer();
			case 2 -> // next page
					NetworkUtil.sendToServer(new PacketGuiSelectRequest(0, 0));
			case 3 -> // previous page
					NetworkUtil.sendToServer(new PacketGuiSelectRequest(1, 0));
			case 4 -> // cycle filter
					NetworkUtil.sendToServer(new PacketGuiSelectRequest(2, 0));
			case 5 -> {
				ITradeStationInfo info = container.getTradeInfo();
				if (info != null) {
					SessionVars.setStringVar("mail.letter.recipient", info.getAddress().getName());
					SessionVars.setStringVar("mail.letter.addressee", EnumAddressee.TRADER.toString());
				}
				player.closeContainer();
			}
		}
	}

	@Override
	protected void addLedgers() {

	}
}
