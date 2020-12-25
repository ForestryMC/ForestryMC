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
package forestry.core.network.packets;

import forestry.core.network.*;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class PacketTileStream extends ForestryPacket implements IForestryPacketClient {
    private final BlockPos pos;
    private final IStreamable streamable;

    public <T extends TileEntity & IStreamable> PacketTileStream(T streamable) {
        this.pos = streamable.getPos();
        this.streamable = streamable;
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.TILE_FORESTRY_UPDATE;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeBlockPos(pos);
        streamable.writeData(data);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
            BlockPos pos = data.readBlockPos();
            IStreamable tile = TileUtil.getTile(player.world, pos, IStreamable.class);
            if (tile != null) {
                tile.readData(data);
            }
        }
    }
}
