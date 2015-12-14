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
package forestry.apiculture.gui.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import net.minecraftforge.common.BiomeDictionary;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;

public class HabitatSlot extends Widget {
	private final Collection<BiomeDictionary.Type> biomes;
	private final String name;
	private final String iconIndex;
	public boolean isActive = false;

	public HabitatSlot(WidgetManager widgetManager, int xPos, int yPos, String name, Collection<BiomeDictionary.Type> biomes) {
		super(widgetManager, xPos, yPos);
		this.biomes = biomes;
		this.name = name;
		this.iconIndex = "habitats/" + name.toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String getLegacyTooltip(EntityPlayer player) {
		return name;
	}

	public IIcon getIcon() {
		return TextureManager.getInstance().getDefault(iconIndex);
	}

	public void setActive(Collection<BiomeDictionary.Type> biomes) {
		isActive = !Collections.disjoint(this.biomes, biomes);
	}

	@Override
	public void draw(int startX, int startY) {
		if (getIcon() != null) {
			if (!isActive) {
				GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.2f);
			} else {
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}

			Proxies.render.bindTexture(SpriteSheet.ITEMS);
			manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, getIcon(), 16, 16);
		}
	}
}
