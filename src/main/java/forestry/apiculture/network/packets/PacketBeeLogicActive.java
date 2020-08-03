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
package forestry.apiculture.network.packets;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.core.network.*;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class PacketBeeLogicActive extends ForestryPacket implements IForestryPacketClient {
    private final BlockPos tilePos;
    private final IBeekeepingLogic beekeepingLogic;

    public PacketBeeLogicActive(IBeeHousing tile) {
        this.tilePos = tile.getCoordinates();
        this.beekeepingLogic = tile.getBeekeepingLogic();
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.BEE_LOGIC_ACTIVE;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeBlockPos(tilePos);
        beekeepingLogic.writeData(data);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
            BlockPos tilePos = data.readBlockPos();

            IBeeHousing beeHousing = TileUtil.getTile(player.world, tilePos, IBeeHousing.class);
            if (beeHousing != null) {
                IBeekeepingLogic beekeepingLogic = beeHousing.getBeekeepingLogic();
                beekeepingLogic.readData(data);
            }
        }
    }
}
