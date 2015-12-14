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
package forestry.factory.blocks;

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.TileForestry;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMillRainmaker;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;

public enum BlockFactoryTesrType implements IMachinePropertiesTESR {
	BOTTLER(TileBottler.class),
	CARPENTER(TileCarpenter.class),
	CENTRIFUGE(TileCentrifuge.class),
	FERMENTER(TileFermenter.class),
	MOISTENER(TileMoistener.class),
	SQUEEZER(TileSqueezer.class),
	STILL(TileStill.class),
	RAINMAKER(TileMillRainmaker.class) {
		@Override
		public IBlockRenderer getRenderer() {
			return Proxies.render.getRenderMill(Constants.TEXTURE_PATH_BLOCKS + "/rainmaker_");
		}
	};

	public static final BlockFactoryTesrType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;
	private final String name;

	BlockFactoryTesrType(Class<? extends TileForestry> teClass) {
		String name = toString().toLowerCase(Locale.ENGLISH);
		this.teIdent = "forestry." + WordUtils.capitalize(name);
		this.teClass = teClass;
		this.name = name;
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
	public IBlockRenderer getRenderer() {
		return Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/" + name + "_");
	}
}
