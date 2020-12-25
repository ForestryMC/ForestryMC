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
package forestry.factory.gui;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.InventoryGhostCrafting;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryContainers;
import forestry.factory.inventory.InventoryFabricator;
import forestry.factory.tiles.TileFabricator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerFabricator extends ContainerLiquidTanks<TileFabricator> implements IContainerCrafting {

    public ContainerFabricator(int windowId, PlayerInventory playerInventory, TileFabricator tile) {
        super(windowId, FactoryContainers.FABRICATOR.containerType(), playerInventory, tile, 8, 129);

        // Internal inventory
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(
                        this.tile,
                        InventoryFabricator.SLOT_INVENTORY_1 + k + i * 9,
                        8 + k * 18,
                        84 + i * 18
                ));
            }
        }

        // Molten resource
        this.addSlot(new SlotFiltered(this.tile, InventoryFabricator.SLOT_METAL, 26, 21));

        // Plan
        this.addSlot(new SlotFiltered(this.tile, InventoryFabricator.SLOT_PLAN, 139, 17));

        // Result
        this.addSlot(new SlotOutput(this.tile, InventoryFabricator.SLOT_RESULT, 139, 53));

        // Crafting matrix
        for (int l = 0; l < 3; l++) {
            for (int k = 0; k < 3; k++) {
                this.addSlot(new SlotCraftMatrix(
                        this,
                        this.tile.getCraftingInventory(),
                        InventoryGhostCrafting.SLOT_CRAFTING_1 + k + l * 3,
                        67 + k * 18,
                        17 + l * 18
                ));
            }
        }
    }

    public static ContainerFabricator fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileFabricator tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileFabricator.class);
        return new ContainerFabricator(windowId, inv, tile);    //TODO nullability.
    }

    @Override
    public void onCraftMatrixChanged(IInventory iinventory, int slot) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateProgressBar(int messageId, int data) {
//        super.updateProgressBar(messageId, data);

        tile.getGUINetworkData(messageId, data);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener crafter : listeners) {
            tile.sendGUINetworkData(this, crafter);
        }
    }

    public TileFabricator getFabricator() {
        return tile;
    }
}
