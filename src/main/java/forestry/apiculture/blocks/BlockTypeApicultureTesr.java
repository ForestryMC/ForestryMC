/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture.blocks;

import forestry.apiculture.features.ApicultureTiles;
import forestry.apiculture.tiles.TileApiaristChest;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileNaturalistChest;
import forestry.modules.features.FeatureTileType;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.function.Supplier;

public enum BlockTypeApicultureTesr implements IBlockTypeTesr {
    APIARIST_CHEST(() -> ApicultureTiles.APIARIST_CHEST, "api_chest", "apiaristchest", TileNaturalistChest.CHEST_SHAPE);

    public static final BlockTypeApicultureTesr[] VALUES = values();

    private final IMachinePropertiesTesr<?> machineProperties;

    <T extends TileApiaristChest> BlockTypeApicultureTesr(
            Supplier<FeatureTileType<? extends T>> teClass,
            String name,
            String textureName,
            VoxelShape shape
    ) {
        MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr.Builder<>(teClass, name)
                .setParticleTexture(name + ".0")
                .setNotFullCube()
                .setShape(shape)
                .create();
        Proxies.render.setRenderChest(machineProperties, textureName);
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
