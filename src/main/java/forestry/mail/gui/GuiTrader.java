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

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.mail.gadgets.MachineTrader;

public class GuiTrader extends GuiForestry<ContainerTrader, MachineTrader> {

	public GuiTrader(InventoryPlayer inventoryplayer, MachineTrader tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mailtrader2.png", new ContainerTrader(inventoryplayer, tile), tile);
		this.xSize = 226;
		this.ySize = 220;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = StringUtil.localizeTile(inventory.getUnlocalizedTitle());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.mail.text"));

		String receive = StringUtil.localize("gui.mail.receive");
		this.fontRendererObj.drawString(receive, getCenteredOffset(receive, 70) + 51, 45, fontColor.get("gui.mail.text"));

		String send = StringUtil.localize("gui.mail.send");
		this.fontRendererObj.drawString(send, getCenteredOffset(send, 70) + 51, 99, fontColor.get("gui.mail.text"));

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		fontRendererObj.drawString(container.getAddress().getName(), guiLeft + 19, guiTop + 22, fontColor.get("gui.mail.text"));
	}
}
