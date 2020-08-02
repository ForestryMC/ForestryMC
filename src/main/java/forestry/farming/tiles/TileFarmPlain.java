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
package forestry.farming.tiles;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import forestry.api.multiblock.IMultiblockController;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmMaterial;
import forestry.farming.features.FarmingTiles;

//TODO: Fix band: currently breaks multiblocks because removes the old tile and adds a new tile, this causes a ConcurrentModificationException (attachBlock, detachBlock)
public class TileFarmPlain extends TileFarm {
    public TileFarmPlain() {
        super(FarmingTiles.PLAIN.tileType());
    }

    @Override
    public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
        super.onMachineAssembled(multiblockController, minCoord, maxCoord);

        // set band block meta
        int bandY = maxCoord.getY() - 1;
        if (getPos().getY() == bandY) {
            EnumFarmMaterial material = EnumFarmMaterial.BRICK_STONE;
            Block block = getBlockState().getBlock();
            if (block instanceof BlockFarm) {
                material = ((BlockFarm) block).getFarmMaterial();
            }
            //this.world.setBlockState(getPos(), FarmingBlocks.FARM.get(EnumFarmBlockType.BAND, material).defaultState(), 2);
        }
    }

    @Override
    public void onMachineBroken() {
        super.onMachineBroken();

        // set band block meta back to normal
        EnumFarmMaterial material = EnumFarmMaterial.BRICK_STONE;
        Block block = getBlockState().getBlock();
        if (block instanceof BlockFarm) {
            material = ((BlockFarm) block).getFarmMaterial();
        }
        //this.world.setBlockState(getPos(), FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, material).defaultState(), 2);
    }
}
