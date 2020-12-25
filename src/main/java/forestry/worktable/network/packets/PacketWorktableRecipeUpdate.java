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
package forestry.worktable.network.packets;

import forestry.core.network.*;
import forestry.core.tiles.TileUtil;
import forestry.worktable.recipes.MemorizedRecipe;
import forestry.worktable.tiles.TileWorktable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Used to sync the worktable crafting result from Server to Client.
 */
public class PacketWorktableRecipeUpdate extends ForestryPacket implements IForestryPacketClient {
    private final BlockPos pos;
    @Nullable
    private final MemorizedRecipe recipe;

    public PacketWorktableRecipeUpdate(TileWorktable worktable) {
        this.pos = worktable.getPos();
        this.recipe = worktable.getCurrentRecipe();
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.WORKTABLE_CRAFTING_UPDATE;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeBlockPos(pos);
        data.writeStreamable(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
            BlockPos pos = data.readBlockPos();
            MemorizedRecipe recipe = data.readStreamable(MemorizedRecipe::new);

            TileUtil.actOnTile(player.world, pos, TileWorktable.class, tile -> tile.setCurrentRecipe(recipe));
        }
    }
}
