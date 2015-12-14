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
package forestry.arboriculture.render;

import java.awt.Color;

import net.minecraft.util.IIcon;

import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.ILeafIconProvider;

public class IconProviderLeaves implements ILeafIconProvider {

	private final TextureLeaves leafTexture;
	private final int color;
	private final int colorPollinated;

	public IconProviderLeaves(EnumLeafType leafType, Color color, Color colorPollinated) {
		this.leafTexture = TextureLeaves.get(leafType);
		this.color = color.getRGB();
		this.colorPollinated = colorPollinated.getRGB();
	}

	@Override
	public int getColor(boolean pollinated) {
		if (pollinated) {
			return colorPollinated;
		} else {
			return color;
		}
	}

	@Override
	public IIcon getIcon(boolean pollinated, boolean fancy) {
		if (pollinated) {
			return leafTexture.getPollinated();
		} else if (fancy) {
			return leafTexture.getFancy();
		} else {
			return leafTexture.getPlain();
		}
	}

}

