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

import forestry.api.farming.DefaultFarmListener;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.multiblock.IFarmComponent;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.features.FarmingTiles;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileFarmControl extends TileFarm implements IFarmComponent.Listener {

    private final IFarmListener farmListener;

    public TileFarmControl() {
        super(FarmingTiles.CONTROL.tileType());
        this.farmListener = new ControlFarmListener(this);
    }

    @Override
    public IFarmListener getFarmListener() {
        return farmListener;
    }

    private static class ControlFarmListener extends DefaultFarmListener {
        private final TileFarmControl tile;

        public ControlFarmListener(TileFarmControl tile) {
            this.tile = tile;
        }

        @Override
        public boolean cancelTask(IFarmLogic logic, FarmDirection direction) {
            for (Direction facing : new Direction[]{Direction.UP, Direction.DOWN, direction.getFacing()}) {
                BlockPos pos = tile.getPos();
                World world = tile.getWorldObj();
                BlockState blockState = world.getBlockState(pos.offset(facing));
                if (!(blockState.getBlock() instanceof BlockFarm) && world.getRedstonePower(pos, facing) > 0) {
                    return true;
                }
            }
            return false;
        }
    }

}
