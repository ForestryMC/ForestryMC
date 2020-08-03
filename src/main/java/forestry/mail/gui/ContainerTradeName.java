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
package forestry.mail.gui;

import forestry.api.mail.IMailAddress;
import forestry.core.gui.ContainerTile;
import forestry.core.tiles.TileUtil;
import forestry.mail.features.MailContainers;
import forestry.mail.tiles.TileTrader;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public class ContainerTradeName extends ContainerTile<TileTrader> {

    public static ContainerTradeName fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileTrader tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileTrader.class);
        return new ContainerTradeName(windowId, inv, tile);    //TODO nullability.
    }

    public ContainerTradeName(int windowId, PlayerInventory inv, TileTrader tile) {
        super(windowId, MailContainers.TRADE_NAME.containerType(), tile);
    }

    public IMailAddress getAddress() {
        return tile.getAddress();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (tile.isLinked()) {
            for (Object crafter : listeners) {
                if (crafter instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) crafter;
                    tile.openGui(player, tile.getPos());    //TODO correct pos?
                }
            }
        }
    }
}
