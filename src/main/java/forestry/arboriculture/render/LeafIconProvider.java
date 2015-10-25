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

import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.ILeafIconProvider;
import forestry.api.core.sprite.ISprite;

public class LeafIconProvider implements ILeafIconProvider {

	private final LeafTexture leafTexture;
	private final int color;
	private final int colorPollinated;

	public LeafIconProvider(EnumLeafType leafType, Color color, Color colorPollinated) {
		this.leafTexture = LeafTexture.get(leafType);
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
	public ISprite getIcon(boolean pollinated, boolean fancy) {
		if (pollinated) {
			return leafTexture.getPollinated();
		} else if (fancy) {
			return leafTexture.getFancy();
		} else {
			return leafTexture.getPlain();
		}
	}

}
