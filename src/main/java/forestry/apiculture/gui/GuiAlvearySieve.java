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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;

public class GuiAlvearySieve extends GuiForestryTitled<ContainerAlvearySieve> {
	private final TileAlvearySieve tile;

	public GuiAlvearySieve(ContainerAlvearySieve container, PlayerInventory inventory, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/sieve.png", container, inventory, container.getTile());
		this.tile = container.getTile();
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
	}
}
