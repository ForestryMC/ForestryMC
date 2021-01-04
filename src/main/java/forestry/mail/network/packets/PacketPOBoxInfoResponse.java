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
package forestry.mail.network.packets;

import forestry.core.network.*;
import forestry.mail.POBoxInfo;
import forestry.mail.gui.GuiMailboxInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketPOBoxInfoResponse extends ForestryPacket implements IForestryPacketClient {
    public final POBoxInfo poboxInfo;

    public PacketPOBoxInfoResponse(POBoxInfo info) {
        this.poboxInfo = info;
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.POBOX_INFO_RESPONSE;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeInt(poboxInfo.playerLetters);
        data.writeInt(poboxInfo.tradeLetters);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {

        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
            POBoxInfo poboxInfo = new POBoxInfo(data.readInt(), data.readInt());
            GuiMailboxInfo.instance.setPOBoxInfo(player, poboxInfo);
        }
    }
}
