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
package forestry.arboriculture.blocks;

import forestry.arboriculture.tiles.TileArboristChest;
import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.proxy.Proxies;
import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.TileForestry;

public enum BlockArboricultureType implements IMachinePropertiesTESR {
	ARBCHEST(TileArboristChest.class, "ArbChest") {
		@Override
		public IBlockRenderer getRenderer() {
			return Proxies.render.getRenderChest("arbchest");
		}
	};

	public static final BlockArboricultureType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockArboricultureType(Class<? extends TileForestry> teClass, String teName) {
		this.teIdent = "forestry." + teName;
		this.teClass = teClass;
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	@Override
	public String getTeIdent() {
		return teIdent;
	}

	@Override
	public Class<? extends TileForestry> getTeClass() {
		return teClass;
	}
}
