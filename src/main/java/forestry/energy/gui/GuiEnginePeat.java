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
package forestry.energy.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.config.Constants;
import forestry.energy.tiles.TileEnginePeat;

public class GuiEnginePeat extends GuiEngine<ContainerEnginePeat, TileEnginePeat> {

	public GuiEnginePeat(ContainerEnginePeat container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/peatengine.png", container, inventory, container.getTile(), title);
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		if (tile.isBurning()) {
			int progress = tile.getBurnTimeRemainingScaled(12);
			blit(transform, leftPos + 45, topPos + 27 + 12 - progress, 176, 12 - progress, 14, progress + 2);
		}
	}
}
