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
package forestry.core.items;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class ItemWithGui extends ItemForestry {

    public ItemWithGui(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (!worldIn.isRemote) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity) playerIn;    //TODO safe?
            openGui(sPlayer, stack);
        }

        return ActionResult.resultSuccess(stack);
    }

    protected void openGui(ServerPlayerEntity player, ItemStack stack) {
        NetworkHooks.openGui(
                player,
                new ContainerProvider(stack),
                buffer -> writeContainerData(player, stack, new PacketBufferForestry(buffer))
        );
    }

    protected void writeContainerData(ServerPlayerEntity player, ItemStack stack, PacketBufferForestry buffer) {
        buffer.writeBoolean(player.getActiveHand() == Hand.MAIN_HAND);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack itemstack, PlayerEntity player) {
        if (!itemstack.isEmpty() &&
            player instanceof ServerPlayerEntity &&
            player.openContainer instanceof ContainerItemInventory) {
            player.closeScreen();
        }

        return super.onDroppedByPlayer(itemstack, player);
    }

    @Nullable
    public abstract Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem);

    public static class ContainerProvider implements INamedContainerProvider {

        private final ItemStack heldItem;

        public ContainerProvider(ItemStack heldItem) {
            this.heldItem = heldItem;
        }

        @Override
        public ITextComponent getDisplayName() {
            return heldItem.getDisplayName();
        }

        @Nullable
        @Override
        public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            Item item = heldItem.getItem();
            if (!(item instanceof ItemWithGui)) {
                return null;
            }

            ItemWithGui itemWithGui = (ItemWithGui) item;
            return itemWithGui.getContainer(windowId, playerEntity, heldItem);
        }
    }

}
