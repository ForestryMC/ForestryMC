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
package forestry.factory.gui;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.slots.*;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryContainers;
import forestry.factory.inventory.InventoryCarpenter;
import forestry.factory.tiles.TileCarpenter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ContainerCarpenter extends ContainerLiquidTanks<TileCarpenter> implements IContainerCrafting {

    public static ContainerCarpenter fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileCarpenter tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileCarpenter.class);
        return new ContainerCarpenter(windowId, inv, tile);    //TODO nullability.
    }

    public ContainerCarpenter(int windowId, PlayerInventory inventoryplayer, TileCarpenter tile) {
        super(windowId, FactoryContainers.CARPENTER.containerType(), inventoryplayer, tile, 8, 136);

        // Internal inventory
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(tile, InventoryCarpenter.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
            }
        }

        // Liquid Input
        this.addSlot(new SlotLiquidIn(tile, InventoryCarpenter.SLOT_CAN_INPUT, 120, 20));
        // Boxes
        this.addSlot(new SlotFiltered(tile, InventoryCarpenter.SLOT_BOX, 83, 20));
        // Product
        this.addSlot(new SlotOutput(tile, InventoryCarpenter.SLOT_PRODUCT, 120, 56));

        // Craft Preview display
        addSlot(new SlotLocked(tile.getCraftPreviewInventory(), 0, 80, 51));

        // Crafting matrix
        for (int l = 0; l < 3; l++) {
            for (int k1 = 0; k1 < 3; k1++) {
                addSlot(new SlotCraftMatrix(this, tile.getCraftingInventory(), k1 + l * 3, 10 + k1 * 18, 20 + l * 18));
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory iinventory, int slot) {
        tile.checkRecipe();
    }

    private ItemStack oldCraftPreview = ItemStack.EMPTY;

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        IInventory craftPreviewInventory = tile.getCraftPreviewInventory();

        ItemStack newCraftPreview = craftPreviewInventory.getStackInSlot(0);
        if (!ItemStack.areItemStacksEqual(oldCraftPreview, newCraftPreview)) {
            oldCraftPreview = newCraftPreview;

            PacketItemStackDisplay packet = new PacketItemStackDisplay(tile, newCraftPreview);
            sendPacketToListeners(packet);
        }
    }

    public TileCarpenter getCarpenter() {
        return tile;
    }

}
