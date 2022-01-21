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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;

public class GuiBackpack extends GuiForestry<ContainerBackpack> {

	public GuiBackpack(ContainerBackpack container, Inventory inv, Component title) {
		super(getTextureString(container), container, inv, title);
		ContainerBackpack.Size size = container.getSize();

		if (size == ContainerBackpack.Size.T2) {
			imageWidth = 176;
			imageHeight = 192;
		}
	}

	private static String getTextureString(ContainerBackpack container) {
		ContainerBackpack.Size size = container.getSize();
		switch (size) {
			case DEFAULT: {
				return Constants.TEXTURE_PATH_GUI + "/backpack.png";
			}
			case T2: {
				return Constants.TEXTURE_PATH_GUI + "/backpack_t2.png";
			}
			default: {
				return Constants.TEXTURE_PATH_GUI + "/backpack.png";
			}
		}
	}

	@Override
	protected void addLedgers() {

	}
}
