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
package forestry.core.blocks;

import com.mojang.authlib.GameProfile;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Log;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BlockForestry extends Block {

    protected BlockForestry(Block.Properties properties) {
        super(properties
                .hardnessAndResistance(1.5f));
    }

    @Override
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        if (world.isRemote) {
            return;
        }

        if (placer instanceof PlayerEntity) {
            TileUtil.actOnTile(world, pos, IOwnedTile.class, tile -> {
                IOwnerHandler ownerHandler = tile.getOwnerHandler();
                PlayerEntity player = (PlayerEntity) placer;
                GameProfile gameProfile = player.getGameProfile();
                ownerHandler.setOwner(gameProfile);
            });
        }
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);

        if (world instanceof World) {
            try {
                TileUtil.actOnTile(
                        world,
                        pos,
                        TileForestry.class,
                        tile -> tile.onNeighborTileChange((World) world, pos, neighbor)
                );
            } catch (StackOverflowError error) {
                Log.error("Stack Overflow Error in BlockForestry.onNeighborChange()", error);
                throw error;
            }
        }
    }
}
