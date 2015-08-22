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
package forestry.core.gui.widgets;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.WidgetManager;
import forestry.core.proxy.Proxies;

public class ReservoirWidget extends TankWidget {

	public ReservoirWidget(WidgetManager manager, int xPos, int yPos, int slot) {
		super(manager, xPos, yPos, slot);
		this.height = 16;
	}

	@Override
	public void draw(int startX, int startY) {

		FluidStack contents = getTank().getFluid();
		if (contents == null || contents.amount <= 0 || contents.getFluid() == null) {
			return;
		}
		TextureAtlasSprite liquidIcon = contents.getFluid().getIcon(contents);
		if (liquidIcon == null) {
			return;
		}

		int squaled = (contents.amount * height) / getTank().getCapacity();
		if (squaled > height) {
			squaled = height;
		}

		Proxies.common.bindTexture();
		int start = 0;

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
		while (true) {
			int x;

			if (squaled > 16) {
				x = 16;
				squaled -= 16;
			} else {
				x = squaled;
				squaled = 0;
			}

			manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos + height - x - start, liquidIcon, 16, 16 - (16 - x));
			start = start + 16;

			if (x == 0 || squaled == 0) {
				break;
			}
		}

	}
}
