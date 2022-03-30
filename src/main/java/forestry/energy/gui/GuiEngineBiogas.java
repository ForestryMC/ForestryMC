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
import forestry.core.gui.widgets.TankWidget;
import forestry.energy.tiles.TileEngineBiogas;

public class GuiEngineBiogas extends GuiEngine<ContainerEngineBiogas, TileEngineBiogas> {
	public GuiEngineBiogas(ContainerEngineBiogas container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/bioengine.png", container, inventory, container.getTile(), title);
		widgetManager.add(new TankWidget(widgetManager, 89, 19, 0));
		widgetManager.add(new TankWidget(widgetManager, 107, 19, 1));

		widgetManager.add(new BiogasSlot(widgetManager, 30, 47, 2));
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		int temperature = tile.getOperatingTemperatureScaled(16);
		if (temperature > 16) {
			temperature = 16;
		}
		if (temperature > 0) {
			blit(transform, leftPos + 53, topPos + 47 + 16 - temperature, 176, 60 + 16 - temperature, 4, temperature);
		}
	}
}
