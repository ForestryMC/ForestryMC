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

import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.network.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketImprintSelectionResponse extends ForestryPacket implements IForestryPacketClient {
    private final int primary;
    private final int secondary;

    public PacketImprintSelectionResponse(int primaryIndex, int secondaryIndex) {
        this.primary = primaryIndex;
        this.secondary = secondaryIndex;
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.IMPRINT_SELECTION_RESPONSE;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeVarInt(primary);
        data.writeVarInt(secondary);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
            Container container = player.openContainer;
            if (container instanceof ContainerImprinter) {
                int primaryIndex = data.readVarInt();
                int secondaryIndex = data.readVarInt();

                ((ContainerImprinter) container).setSelection(primaryIndex, secondaryIndex);
            }
        }
    }
}
