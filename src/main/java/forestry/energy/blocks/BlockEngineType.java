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
package forestry.energy.blocks;

import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.TileForestry;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEngineClockwork;
import forestry.energy.tiles.TileEngineElectric;
import forestry.energy.tiles.TileEnginePeat;
import forestry.energy.tiles.TileGenerator;
import forestry.plugins.PluginEnergy;

public enum BlockEngineType implements IMachinePropertiesTESR {
	ELECTRIC(TileEngineElectric.class, "EngineTin", "/engine_tin_"),
	PEAT(TileEnginePeat.class, "EngineCopper", "/engine_copper_"),
	BIOGAS(TileEngineBiogas.class, "EngineBronze", "/engine_bronze_"),
	GENERATOR(TileGenerator.class, "Generator", "/generator_") {
		@Override
		public IBlockRenderer getRenderer() {
			return Proxies.render.getRenderDefaultMachine(texturePath);
		}
	},
	CLOCKWORK(TileEngineClockwork.class, "EngineClockwork", "/engine_clock_");

	public static final BlockEngineType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;
	protected final String texturePath;

	BlockEngineType(Class<? extends TileForestry> teClass, String teName, String textureName) {
		this.teIdent = "forestry." + teName;
		this.teClass = teClass;
		this.texturePath = Constants.TEXTURE_PATH_BLOCKS + textureName;
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
		return PluginEnergy.proxy.getRenderDefaultEngine(texturePath);
	}
}
