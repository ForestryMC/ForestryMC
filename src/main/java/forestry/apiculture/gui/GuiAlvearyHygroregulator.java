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
package forestry.apiculture.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;

public class GuiAlvearyHygroregulator extends GuiForestryTitled<ContainerAlvearyHygroregulator> {
	private final TileAlvearyHygroregulator tile;

	public GuiAlvearyHygroregulator(ContainerAlvearyHygroregulator container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/hygroregulator.png", container, inventory, title);
		this.tile = container.getTile();

		widgetManager.add(new TankWidget(this.widgetManager, 104, 17, 0));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
	}
}
