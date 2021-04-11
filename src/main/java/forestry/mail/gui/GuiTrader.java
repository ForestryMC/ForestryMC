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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import forestry.mail.tiles.TileTrader;

public class GuiTrader extends GuiForestry<ContainerTrader> {
	private final TileTrader tile;

	public GuiTrader(ContainerTrader container, PlayerInventory inv, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/mailtrader2.png", container, inv, title);
		this.tile = container.getTile();
		this.imageWidth = 226;
		this.imageHeight = 220;
	}

	@Override
	protected void renderLabels(MatrixStack transform, int mouseX, int mouseY) {
		String name = Translator.translateToLocal(tile.getUnlocalizedTitle());
		this.minecraft.font.draw(transform, name, textLayout.getCenteredOffset(name), 6, ColourProperties.INSTANCE.get("gui.mail.text"));

		String receive = Translator.translateToLocal("for.gui.mail.receive");
		this.minecraft.font.draw(transform, receive, textLayout.getCenteredOffset(receive, 70) + 51, 45, ColourProperties.INSTANCE.get("gui.mail.text"));

		String send = Translator.translateToLocal("for.gui.mail.send");
		this.minecraft.font.draw(transform, send, textLayout.getCenteredOffset(send, 70) + 51, 99, ColourProperties.INSTANCE.get("gui.mail.text"));

		super.renderLabels(transform, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		this.minecraft.font.draw(transform, container.getAddress().getName(), leftPos + 19, topPos + 22, ColourProperties.INSTANCE.get("gui.mail.text"));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("trade.station");
		addOwnerLedger(tile);
	}
}
