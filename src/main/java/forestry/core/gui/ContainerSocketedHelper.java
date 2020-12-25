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
package forestry.core.gui;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitBoard;
import forestry.core.circuits.ISocketable;
import forestry.core.circuits.ISolderingIron;
import forestry.core.network.packets.PacketChipsetClick;
import forestry.core.network.packets.PacketSocketUpdate;
import forestry.core.network.packets.PacketSolderingIronClick;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NetworkUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerSocketedHelper<T extends TileEntity & ISocketable> implements IContainerSocketed {

    private final T tile;

    public ContainerSocketedHelper(T tile) {
        this.tile = tile;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleChipsetClick(int slot) {
        NetworkUtil.sendToServer(new PacketChipsetClick(slot));
    }

    @Override
    public void handleChipsetClickServer(int slot, ServerPlayerEntity player, ItemStack itemstack) {
        if (!tile.getSocket(slot).isEmpty()) {
            return;
        }

        if (!ChipsetManager.circuitRegistry.isChipset(itemstack)) {
            return;
        }

        ICircuitBoard circuitBoard = ChipsetManager.circuitRegistry.getCircuitBoard(itemstack);
        if (circuitBoard == null) {
            return;
        }

        if (!tile.getSocketType().equals(circuitBoard.getSocketType())) {
            return;
        }

        ItemStack toSocket = itemstack.copy();
        toSocket.setCount(1);
        tile.setSocket(slot, toSocket);

        ItemStack stack = player.inventory.getItemStack();
        stack.shrink(1);
        player.updateHeldItem();

        PacketSocketUpdate packet = new PacketSocketUpdate(tile);
        NetworkUtil.sendToPlayer(packet, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleSolderingIronClick(int slot) {
        NetworkUtil.sendToServer(new PacketSolderingIronClick(slot));
    }

    @Override
    public void handleSolderingIronClickServer(int slot, ServerPlayerEntity player, ItemStack itemstack) {
        ItemStack socket = tile.getSocket(slot);
        if (socket.isEmpty() || !(itemstack.getItem() instanceof ISolderingIron)) {
            return;
        }

        // Not sufficient space in player's inventory. failed to stow.
        if (!InventoryUtil.stowInInventory(socket, player.inventory, false)) {
            return;
        }

        tile.setSocket(slot, ItemStack.EMPTY);
        InventoryUtil.stowInInventory(socket, player.inventory, true);
        itemstack.damageItem(1, player, p -> p.sendBreakAnimation(p.getActiveHand()));    //TODO onBreak
        player.updateHeldItem();

        PacketSocketUpdate packet = new PacketSocketUpdate(tile);
        NetworkUtil.sendToPlayer(packet, player);
    }
}
