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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

public class HabitatSlot extends Widget {
	@Nonnull
	private final Collection<BiomeDictionary.Type> biomes;
	@Nonnull
	private final String name;
	@Nonnull
	private final String iconIndex;
	public boolean isActive = false;

	public HabitatSlot(@Nonnull WidgetManager widgetManager, int xPos, int yPos, @Nonnull String name, @Nonnull Collection<BiomeDictionary.Type> biomes) {
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
		return TextureManager.getInstance().getDefault(iconIndex);
	}

	public void setActive(Collection<BiomeDictionary.Type> biomes) {
		isActive = !Collections.disjoint(this.biomes, biomes);
	}

	@Override
	public void draw(int startX, int startY) {
		if (getIcon() != null) {
			if (!isActive) {
				GlStateManager.color(0.2f, 0.2f, 0.2f, 0.2f);
			} else {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			}

			Proxies.render.bindTexture(TextureMap.locationBlocksTexture);
			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, getIcon(), 16, 16);
		}
	}
}
