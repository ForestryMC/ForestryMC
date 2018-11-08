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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraftforge.common.BiomeDictionary;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.TextureManagerForestry;

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
	public ToolTip getToolTip(int mouseX, int mouseY) {
		ToolTip tooltip = new ToolTip();
		tooltip.add(name);
		return tooltip;
	}

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return TextureManagerForestry.getInstance().getDefault(iconIndex);
	}

	public void setActive(Collection<BiomeDictionary.Type> biomes) {
		isActive = !Collections.disjoint(this.biomes, biomes);
	}

	@Override
	public void draw(int startX, int startY) {
		if (!isActive) {
			GlStateManager.color(0.2f, 0.2f, 0.2f, 0.2f);
		} else {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		}

		TextureManagerForestry.getInstance().bindGuiTextureMap();
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, getIcon(), 16, 16);
	}
}
