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

import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileMill;
import forestry.factory.features.FactoryTiles;
import forestry.modules.features.FeatureTileType;

import java.util.function.Supplier;

public enum BlockTypeFactoryTesr implements IBlockTypeTesr {
    BOTTLER(() -> FactoryTiles.BOTTLER, "bottler"),
    CARPENTER(() -> FactoryTiles.CARPENTER, "carpenter"),
    CENTRIFUGE(() -> FactoryTiles.CENTRIFUGE, "centrifuge"),
    FERMENTER(() -> FactoryTiles.FERMENTER, "fermenter"),
    MOISTENER(() -> FactoryTiles.MOISTENER, "moistener"),
    SQUEEZER(() -> FactoryTiles.SQUEEZER, "squeezer"),
    STILL(() -> FactoryTiles.STILL, "still"),
    RAINMAKER(() -> FactoryTiles.RAINMAKER, "rainmaker", Constants.TEXTURE_PATH_BLOCK + "/rainmaker_");

	public static final BlockTypeFactoryTesr[] VALUES = values();

	private final IMachinePropertiesTesr<?> machineProperties;

    <T extends TileBase> BlockTypeFactoryTesr(Supplier<FeatureTileType<? extends T>> teClass, String name) {
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":block/" + name + ".0");
		Proxies.render.setRenderDefaultMachine(machineProperties, Constants.TEXTURE_PATH_BLOCK + "/" + name + "_");
		this.machineProperties = machineProperties;
	}

    <T extends TileMill> BlockTypeFactoryTesr(Supplier<FeatureTileType<? extends T>> teClass, String name, String renderMillTexture) {
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":block/" + name + ".0");
		Proxies.render.setRenderMill(machineProperties, renderMillTexture);
		this.machineProperties = machineProperties;
	}

	@Override
	public IMachinePropertiesTesr<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
