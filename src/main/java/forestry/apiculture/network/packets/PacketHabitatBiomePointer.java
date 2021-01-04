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
package forestry.apiculture.network.packets;

import forestry.core.network.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketHabitatBiomePointer extends ForestryPacket implements IForestryPacketClient {
    private final BlockPos pos;

    public PacketHabitatBiomePointer(BlockPos coordinates) {
        this.pos = coordinates;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeBlockPos(pos);
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.HABITAT_BIOME_POINTER;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
            BlockPos pos = data.readBlockPos();
            //TextureHabitatLocator.getInstance().setTargetCoordinates(pos);//TODO: TextureHabitatLocator
        }
    }
}
