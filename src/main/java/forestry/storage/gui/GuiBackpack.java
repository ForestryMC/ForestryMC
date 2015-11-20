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
package forestry.storage.gui;

import net.minecraft.inventory.IInventory;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;

public class GuiBackpack extends GuiForestry<ContainerBackpack, IInventory> {

	public GuiBackpack(ContainerBackpack container) {
		this(Constants.TEXTURE_PATH_GUI + "/backpack.png", container);
	}

	protected GuiBackpack(String texture, ContainerBackpack container) {
		super(texture, container, null);
	}
}
