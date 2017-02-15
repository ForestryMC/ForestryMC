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
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;

public enum BlockTypeCoreTesr implements IBlockTypeTesr {
	ANALYZER(createAnalyzerProperties(TileAnalyzer.class, "analyzer")),
	ESCRITOIRE(createEscritoireProperties(TileEscritoire.class, "escritoire"));

	public static final BlockTypeCoreTesr[] VALUES = values();

	private final IMachinePropertiesTesr machineProperties;

	private static IMachinePropertiesTesr<? extends TileAnalyzer> createAnalyzerProperties(Class<? extends TileAnalyzer> teClass, String name) {
		MachinePropertiesTesr<? extends TileAnalyzer> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":blocks/" + name + ".0");
		Proxies.render.setRendererAnalyzer(machineProperties);
		return machineProperties;
	}

	private static IMachinePropertiesTesr<? extends TileEscritoire> createEscritoireProperties(Class<? extends TileEscritoire> teClass, String name) {
		MachinePropertiesTesr<? extends TileEscritoire> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":blocks/" + name + ".0");
		Proxies.render.setRenderEscritoire(machineProperties);
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
