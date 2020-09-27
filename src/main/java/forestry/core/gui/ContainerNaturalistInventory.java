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
package forestry.core.gui;

import forestry.core.features.CoreContainers;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.tiles.IFilterSlotDelegate;
import forestry.core.tiles.TileNaturalistChest;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;

public class ContainerNaturalistInventory extends ContainerTile<TileNaturalistChest> implements IGuiSelectable {

    //TODO more duped code
    public static ContainerNaturalistInventory fromNetwork(
            int windowId,
            PlayerInventory playerInv,
            PacketBuffer extraData
    ) {
        TileNaturalistChest tile = TileUtil.getTile(
                playerInv.player.world,
                extraData.readBlockPos(),
                TileNaturalistChest.class
        );    //TODO think this is OK for inheritance
        return new ContainerNaturalistInventory(windowId, playerInv, tile, extraData.readVarInt());
    }

    private final int page;
    private final int maxPage;

    public ContainerNaturalistInventory(int windowId, PlayerInventory player, TileNaturalistChest tile, int page) {
        super(windowId, CoreContainers.NATURALIST_INVENTORY.containerType(), player, tile, 18, 120);

        this.page = page;
        this.maxPage = 5;
        addInventory(this, tile, page);
    }

    //TODO this is hardcoded to max page. So is the maxPage field needed??
    public static <T extends IInventory & IFilterSlotDelegate> void addInventory(
            ContainerForestry container,
            T inventory,
            int selectedPage
    ) {
        for (int page = 0; page < 5; page++) {
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    int slot = y + page * 25 + x * 5;
                    if (page == selectedPage) {
                        container.addSlot(new SlotFilteredInventory(inventory, slot, 100 + y * 18, 21 + x * 18));
                    } else {
                        container.addSlot(new SlotFilteredInventory(inventory, slot, -10000, -10000));
                    }
                }
            }
        }
    }

    @Override
    public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
        tile.flipPage(player, (short) primary);
    }

    public int getPage() {
        return page;
    }

    public int getMaxPage() {
        return maxPage;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        tile.increaseNumPlayersUsing();
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);
        tile.decreaseNumPlayersUsing();
    }
}
