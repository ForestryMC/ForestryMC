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
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMillRainmaker;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;

public enum BlockTypeFactoryTesr implements IBlockTypeTesr {
	BOTTLER(TileBottler.class, "bottler"),
	CARPENTER(TileCarpenter.class, "carpenter"),
	CENTRIFUGE(TileCentrifuge.class, "centrifuge"),
	FERMENTER(TileFermenter.class, "fermenter"),
	MOISTENER(TileMoistener.class, "moistener"),
	SQUEEZER(TileSqueezer.class, "squeezer"),
	STILL(TileStill.class, "still"),
	RAINMAKER(TileMillRainmaker.class, "rainmaker", Constants.TEXTURE_PATH_BLOCK + "/rainmaker_");

	public static final BlockTypeFactoryTesr[] VALUES = values();

	private final IMachinePropertiesTesr<?> machineProperties;

	<T extends TileBase> BlockTypeFactoryTesr(Class<T> teClass, String name) {
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":blocks/" + name + ".0");
		Proxies.render.setRenderDefaultMachine(machineProperties, Constants.TEXTURE_PATH_BLOCK + "/" + name + "_");
		this.machineProperties = machineProperties;
	}

	<T extends TileMill> BlockTypeFactoryTesr(Class<T> teClass, String name, String renderMillTexture) {
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr<>(teClass, name, Constants.MOD_ID + ":blocks/" + name + ".0");
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
