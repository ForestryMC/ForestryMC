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
package forestry.lepidopterology.blocks;

import java.util.Locale;

import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.proxy.Proxies;
import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.TileForestry;
import forestry.lepidopterology.tiles.TileLepidopteristChest;
import net.minecraft.util.IStringSerializable;

public enum BlockLepidopterologyType implements IMachinePropertiesTESR, IStringSerializable {
	LEPICHEST(TileLepidopteristChest.class, "LepiChest") {
		@Override
		public IBlockRenderer getRenderer() {
			return Proxies.render.getRenderChest("lepichest");
		}
	};

	public static final BlockLepidopterologyType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockLepidopterologyType(Class<? extends TileForestry> teClass, String teName) {
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
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
