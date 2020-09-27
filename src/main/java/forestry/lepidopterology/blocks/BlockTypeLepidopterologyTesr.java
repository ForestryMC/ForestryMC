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

import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileNaturalistChest;
import forestry.lepidopterology.features.LepidopterologyTiles;
import forestry.modules.features.FeatureTileType;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.function.Supplier;

public enum BlockTypeLepidopterologyTesr implements IBlockTypeTesr {
    LEPICHEST(
            () -> LepidopterologyTiles.LEPIDOPTERIST_CHEST,
            "lepi_chest",
            "lepichest",
            TileNaturalistChest.CHEST_SHAPE
    );

    public static final BlockTypeLepidopterologyTesr[] VALUES = values();

    private final IMachinePropertiesTesr<?> machineProperties;

    <T extends TileNaturalistChest> BlockTypeLepidopterologyTesr(
            Supplier<FeatureTileType<? extends T>> teClass,
            String name,
            String renderName,
            VoxelShape shape
    ) {
        MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr.Builder<>(teClass, name)
                .setParticleTexture(name + ".0")
                .setNotFullCube()
                .setShape(shape)
                .create();
        Proxies.render.setRenderChest(machineProperties, renderName);
        this.machineProperties = machineProperties;
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
