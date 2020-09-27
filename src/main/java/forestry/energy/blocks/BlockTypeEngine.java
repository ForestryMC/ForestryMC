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

import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileBase;
import forestry.energy.ModuleEnergy;
import forestry.energy.features.EnergyTiles;
import forestry.energy.tiles.TileEngine;
import forestry.modules.features.FeatureTileType;

import java.util.function.Supplier;

public enum BlockTypeEngine implements IBlockTypeTesr {
    PEAT(createEngineProperties(() -> EnergyTiles.PEAT_ENGINE, "peat", "/engine_copper")),
    BIOGAS(createEngineProperties(() -> EnergyTiles.BIOGAS_ENGINE, "biogas", "/engine_bronze")),
    CLOCKWORK(createEngineProperties(() -> EnergyTiles.CLOCKWORK_ENGINE, "clockwork", "/engine_clock")),
	/*ELECTRICAL(createEngineProperties(TileEngineElectric.class, "electrical", "/engine_tin")),
	GENERATOR(createMachineProperties(TileEuGenerator.class, "generator", "/generator"))*/;

    public static final BlockTypeEngine[] VALUES = values();

    private final IMachinePropertiesTesr<?> machineProperties;

    BlockTypeEngine(IMachinePropertiesTesr<?> machineProperties) {
        this.machineProperties = machineProperties;
    }

    protected static IMachinePropertiesTesr<?> createEngineProperties(
            Supplier<FeatureTileType<? extends TileEngine>> teClass,
            String name,
            String textureName
    ) {
        MachinePropertiesTesr<? extends TileEngine> machinePropertiesEngine = new MachinePropertiesTesr.Builder<>(
                teClass,
                name
        )
                .setParticleTexture(textureName + ".0")
                .setNotFullCube()
                .create();
        ModuleEnergy.proxy.setRenderDefaultEngine(
                machinePropertiesEngine,
                Constants.TEXTURE_PATH_BLOCK + textureName + "_"
        );
        return machinePropertiesEngine;
    }

    protected static IMachinePropertiesTesr<?> createMachineProperties(
            Supplier<FeatureTileType<? extends TileBase>> teClass,
            String name,
            String textureName
    ) {
        MachinePropertiesTesr<? extends TileBase> machinePropertiesTesr = new MachinePropertiesTesr.Builder<>(
                teClass,
                name
        )
                .setParticleTexture(textureName + ".0")
                .create();
        Proxies.render.setRenderDefaultMachine(machinePropertiesTesr, Constants.TEXTURE_PATH_BLOCK + textureName + "_");
        return machinePropertiesTesr;
    }

    @Override
    public IMachinePropertiesTesr<?> getMachineProperties() {
        return machineProperties;
    }

    @Override
    public String getString() {
        return getMachineProperties().getString();
    }
}
