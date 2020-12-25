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

import forestry.core.gui.IGuiSelectable;
import forestry.core.network.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;

public class PacketGuiSelectRequest extends ForestryPacket implements IForestryPacketServer {
    private final int primaryIndex;
    private final int secondaryIndex;

    public PacketGuiSelectRequest(int primaryIndex, int secondaryIndex) {
        this.primaryIndex = primaryIndex;
        this.secondaryIndex = secondaryIndex;
    }

    @Override
    public PacketIdServer getPacketId() {
        return PacketIdServer.GUI_SELECTION_REQUEST;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeVarInt(primaryIndex);
        data.writeVarInt(secondaryIndex);
    }

    public static class Handler implements IForestryPacketHandlerServer {
        @Override
        public void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) {
            int primary = data.readVarInt();
            int secondary = data.readVarInt();

            Container container = player.openContainer;
            if ((container instanceof IGuiSelectable)) {
                IGuiSelectable guiSelectable = (IGuiSelectable) container;
                guiSelectable.handleSelectionRequest(player, primary, secondary);
            }
        }
    }
}
