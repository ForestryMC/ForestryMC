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
package forestry.factory.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileSqueezer;

public class GuiSqueezer extends GuiForestryTitled<ContainerSqueezer> {
	private final TileSqueezer tile;

	public GuiSqueezer(ContainerSqueezer container, PlayerInventory inventory, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/squeezersocket.png", container, inventory, container.getTile());
		this.tile = container.getTile();
		widgetManager.add(new TankWidget(this.widgetManager, 122, 18, 0));
		widgetManager.add(new SocketWidget(this.widgetManager, 75, 20, tile, 0));
	}

	@Override
	protected void drawWidgets() {
		int progress = tile.getProgressScaled(43);
		blit(75, 41, 176, 60, progress, 18);

		super.drawWidgets();
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addPowerLedger(tile.getEnergyManager());
		addHintLedger("squeezer");
	}
}
