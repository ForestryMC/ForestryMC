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
package forestry.core.entities;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.util.ITeleporter;

import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.IFilterSlotDelegate;
import forestry.core.utils.InventoryUtil;

//TODO: large type hierarchy here. If no other modules use other than apiculture then compress this.
public abstract class MinecartEntityContainerForestry extends MinecartEntityForestry implements WorldlyContainer, IFilterSlotDelegate, MenuProvider {
	/**
	 * When set to true, the minecart will drop all items when setDead() is called. When false (such as when travelling
	 * dimensions) it preserves its contents.
	 */
	private boolean dropContentsWhenDead = true;

	public MinecartEntityContainerForestry(EntityType<? extends MinecartEntityContainerForestry> type, Level world) {
		super(type, world);
	}

	public MinecartEntityContainerForestry(EntityType<?> type, Level world, double posX, double posY, double posZ) {
		super(type, world, posX, posY, posZ);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compoundNBT) {
		super.readAdditionalSaveData(compoundNBT);
		getInternalInventory().read(compoundNBT);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compoundNBT) {
		super.addAdditionalSaveData(compoundNBT);
		getInternalInventory().write(compoundNBT);
	}

	@Override
	public void remove(RemovalReason reason) {
		if (reason == RemovalReason.KILLED && dropContentsWhenDead && !level.isClientSide) {
			Containers.dropContents(level, this, getInternalInventory());
		}

		super.remove(reason);
	}

	//TODO tbh super() method looks pretty good too
	@Override
	protected void applyNaturalSlowdown() {
		int redstoneLevel = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
		double drag = 0.98F + redstoneLevel * 0.001F;
		this.setDeltaMovement(this.getDeltaMovement().multiply(drag, 0.0D, drag));
	}

	@Nullable
	@Override
	public Entity changeDimension(ServerLevel p_241206_1_, ITeleporter teleporter) {
		this.dropContentsWhenDead = false;
		return super.changeDimension(p_241206_1_, teleporter);
	}

	/* IInventory */

	protected abstract IInventoryAdapter getInternalInventory();

	@Override
	public boolean isEmpty() {
		return getInternalInventory().isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return isAlive() && player.distanceTo(this) <= 64.0D;
	}

	@Override
	public int getContainerSize() {
		return getInternalInventory().getContainerSize();
	}

	@Override
	public final ItemStack getItem(int slotIndex) {
		return getInternalInventory().getItem(slotIndex);
	}

	@Override
	public ItemStack removeItem(int slotIndex, int amount) {
		return getInternalInventory().removeItem(slotIndex, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
		return getInternalInventory().removeItemNoUpdate(slotIndex);
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
		getInternalInventory().setItem(slotIndex, itemstack);
	}

	@Override
	public final int getMaxStackSize() {
		return getInternalInventory().getMaxStackSize();
	}

	@Override
	public final void startOpen(Player player) {
		getInternalInventory().startOpen(player);
	}

	@Override
	public final void stopOpen(Player player) {
		getInternalInventory().stopOpen(player);
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("MINECART_TITLE_GOES_HERE");
		//TODO inventory names return getInternalInventory().getDisplayName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public final boolean canPlaceItem(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canPlaceItem(slotIndex, itemStack);
	}

	@Override
	public final boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return getInternalInventory().isLocked(slotIndex);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return getInternalInventory().getSlotsForFace(side);
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return getInternalInventory().canPlaceItemThroughFace(slot, stack, side);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return getInternalInventory().canTakeItemThroughFace(slot, stack, side);
	}

	@Override
	public void setChanged() {

	}

	@Override
	public void clearContent() {
		getInternalInventory().clearContent();
	}
}
