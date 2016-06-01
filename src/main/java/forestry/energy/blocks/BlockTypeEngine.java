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

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEngine;
import forestry.energy.PluginEnergy;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEngineClockwork;
import forestry.energy.tiles.TileEngineElectric;
import forestry.energy.tiles.TileEnginePeat;
import forestry.energy.tiles.TileEuGenerator;

public enum BlockTypeEngine implements IBlockTypeTesr {
	PEAT(createEngineProperties(TileEnginePeat.class, "peat", "/engine_copper")),
	BIOGAS(createEngineProperties(TileEngineBiogas.class, "biogas", "/engine_bronze")),
	CLOCKWORK(createEngineProperties(TileEngineClockwork.class, "clockwork", "/engine_clock")),
	ELECTRICAL(createEngineProperties(TileEngineElectric.class, "electrical", "/engine_tin")),
	GENERATOR(createMachineProperties(TileEuGenerator.class, "generator", "/generator"));

	public static final BlockTypeEngine[] VALUES = values();

	@Nonnull
	private final IMachinePropertiesTesr<?> machineProperties;

	BlockTypeEngine(@Nonnull IMachinePropertiesTesr<?> machineProperties) {
		this.machineProperties = machineProperties;
	}

	protected static IMachinePropertiesTesr<?> createEngineProperties(@Nonnull Class<? extends TileEngine> teClass, @Nonnull String name, @Nonnull String textureName) {
		TileEntitySpecialRenderer<TileEngine> renderer = PluginEnergy.proxy.getRenderDefaultEngine(Constants.TEXTURE_PATH_BLOCKS + textureName + "_");
		return new MachinePropertiesEngine<>(teClass, name, renderer, Constants.MOD_ID + ":blocks" + textureName + ".0");
	}

	protected static IMachinePropertiesTesr<?> createMachineProperties(@Nonnull Class<? extends TileBase> teClass, @Nonnull String name, @Nonnull String textureName) {
		TileEntitySpecialRenderer<TileBase> renderer = Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + textureName + "_");
		return new MachinePropertiesTesr<>(teClass, name, renderer, Constants.MOD_ID + ":blocks" + textureName + ".0");
	}

	@Nonnull
	@Override
	public IMachinePropertiesTesr<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
