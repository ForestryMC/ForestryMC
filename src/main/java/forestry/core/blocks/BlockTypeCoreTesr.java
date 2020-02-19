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
package forestry.core.blocks;

import forestry.core.config.Constants;
import forestry.core.features.CoreTiles;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.modules.features.FeatureTileType;

import java.util.function.Supplier;

public enum BlockTypeCoreTesr implements IBlockTypeTesr {
    ANALYZER(createAnalyzerProperties(() -> CoreTiles.ANALYZER, "analyzer")),
    ESCRITOIRE(createEscritoireProperties(() -> CoreTiles.ESCRITOIRE, "escritoire"));

	public static final BlockTypeCoreTesr[] VALUES = values();

	private final IMachinePropertiesTesr machineProperties;

    private static IMachinePropertiesTesr<? extends TileAnalyzer> createAnalyzerProperties(Supplier<FeatureTileType<? extends TileAnalyzer>> teClass, String name) {
		MachinePropertiesTesr<? extends TileAnalyzer> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":block/" + name + ".0");
		Proxies.render.setRendererAnalyzer(machineProperties);    //TODO distexecutor
		return machineProperties;
	}

    private static IMachinePropertiesTesr<? extends TileEscritoire> createEscritoireProperties(Supplier<FeatureTileType<? extends TileEscritoire>> teClass, String name) {
		MachinePropertiesTesr<? extends TileEscritoire> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":block/" + name + ".0");
		Proxies.render.setRenderEscritoire(machineProperties); //TODO distexecutor
		return machineProperties;
	}

	BlockTypeCoreTesr(IMachinePropertiesTesr machineProperties) {
		this.machineProperties = machineProperties;
	}

	@Override
	public IMachinePropertiesTesr getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
