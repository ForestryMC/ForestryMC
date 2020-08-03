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
package forestry.apiculture.gui;

import forestry.apiculture.features.ApicultureContainers;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerAlvearySwarmer extends ContainerTile<TileAlvearySwarmer> {

    public static ContainerAlvearySwarmer fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileAlvearySwarmer tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileAlvearySwarmer.class);
        return new ContainerAlvearySwarmer(windowId, inv, tile);    //TODO nullability.
    }

    public ContainerAlvearySwarmer(int windowId, PlayerInventory player, TileAlvearySwarmer tile) {
        super(windowId, ApicultureContainers.ALVEARY_SWARMER.containerType(), player, tile, 8, 87);

        this.addSlot(new SlotFiltered(tile, 0, 79, 52));
        this.addSlot(new SlotFiltered(tile, 1, 100, 39));
        this.addSlot(new SlotFiltered(tile, 2, 58, 39));
        this.addSlot(new SlotFiltered(tile, 3, 79, 26));
    }
}
