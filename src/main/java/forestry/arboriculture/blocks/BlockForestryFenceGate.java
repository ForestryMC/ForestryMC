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
package forestry.arboriculture.blocks;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockForestryFenceGate extends FenceGateBlock implements IWoodTyped {

    private final boolean fireproof;
    private final IWoodType woodType;

    public BlockForestryFenceGate(boolean fireproof, IWoodType woodType) {
        super(Block.Properties.create(Material.WOOD)
                              .hardnessAndResistance(woodType.getHardness(), woodType.getHardness() * 1.5F)
                              .sound(SoundType.WOOD));
        this.fireproof = fireproof;
        this.woodType = woodType;

        //		setCreativeTab(Tabs.tabArboriculture);	TODO creative tab
    }

    @Override
    public boolean isFireproof() {
        return fireproof;
    }

    @Override
    public IWoodType getWoodType() {
        return woodType;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        if (fireproof) {
            return 0;
        }
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        if (fireproof) {
            return 0;
        }
        return 5;
    }

    @Override
    public WoodBlockKind getBlockKind() {
        return WoodBlockKind.FENCE_GATE;
    }
}