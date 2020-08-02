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

import java.util.function.Supplier;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.factory.features.FactoryTiles;
import forestry.modules.features.FeatureTileType;

public enum BlockTypeFactoryPlain implements IBlockType {
    FABRICATOR(() -> FactoryTiles.FABRICATOR, "fabricator"),
    RAINTANK(() -> FactoryTiles.RAIN_TANK, "raintank");

    public static final BlockTypeFactoryPlain[] VALUES = values();

    private final IMachineProperties machineProperties;

    <T extends TileForestry> BlockTypeFactoryPlain(Supplier<FeatureTileType<? extends T>> teClass, String name) {
        this.machineProperties = new MachineProperties.Builder<>(teClass, name).create();
    }

    @Override
    public IMachineProperties getMachineProperties() {
        return machineProperties;
    }

    @Override
    public String getString() {
        return getMachineProperties().getString();
    }
}
