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
package forestry.core.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.render.ColourProperties;

public abstract class GuiForestryTitled<C extends AbstractContainerMenu> extends GuiForestry<C> {

	protected GuiForestryTitled(String texture, C container, Inventory inv, Component title) {
		super(texture, container, inv, title);
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(transform, partialTicks, mouseX, mouseY);

		textLayout.line = 6;
		if (centeredTitle()) {
			textLayout.drawCenteredLine(transform, title.getString(), 0, ColourProperties.INSTANCE.get("gui.title"));
		} else {
			textLayout.drawLine(transform, title.getString(), 8, ColourProperties.INSTANCE.get("gui.title"));
		}
		bindTexture(textureFile);
	}

	protected boolean centeredTitle() {
		return true;
	}
}
