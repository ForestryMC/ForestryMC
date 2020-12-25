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
import forestry.core.utils.NetworkUtil;
import forestry.worktable.gui.ContainerWorktable;
import forestry.worktable.recipes.MemorizedRecipe;
import forestry.worktable.tiles.TileWorktable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

/**
 * Used to sync the worktable crafting grid from Client to Server.
 */
public class PacketWorktableRecipeRequest extends ForestryPacket implements IForestryPacketServer {
    private final BlockPos pos;
    private final MemorizedRecipe recipe;

    public PacketWorktableRecipeRequest(TileWorktable worktable, MemorizedRecipe recipe) {
        this.pos = worktable.getPos();
        this.recipe = recipe;
    }

    @Override
    public PacketIdServer getPacketId() {
        return PacketIdServer.WORKTABLE_RECIPE_REQUEST;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeBlockPos(pos);
        recipe.writeData(data);
    }

    public static class Handler implements IForestryPacketHandlerServer {

        @Override
        public void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) throws IOException {
            BlockPos pos = data.readBlockPos();
            MemorizedRecipe recipe = new MemorizedRecipe(data);

            TileUtil.actOnTile(player.world, pos, TileWorktable.class, worktable -> {
                worktable.setCurrentRecipe(recipe);

                if (player.openContainer instanceof ContainerWorktable) {
                    ContainerWorktable containerWorktable = (ContainerWorktable) player.openContainer;
                    containerWorktable.updateCraftMatrix();
                }

                NetworkUtil.sendNetworkPacket(new PacketWorktableRecipeUpdate(worktable), pos, player.world);
            });
        }
    }
}
