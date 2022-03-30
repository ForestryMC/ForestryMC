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
package forestry.core.multiblock;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.ILocatable;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;

public abstract class MultiblockControllerForestry extends MultiblockControllerBase implements WorldlyContainer, IOwnedTile, IErrorLogicSource, ILocatable {
	private final OwnerHandler ownerHandler;
	private final IErrorLogic errorLogic;

	protected MultiblockControllerForestry(Level world) {
		super(world);

		this.ownerHandler = new OwnerHandler();
		this.errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public Level getWorldObj() {
		return world;
	}

	@Override
	protected void onMachineAssembled() {
		super.onMachineAssembled();

		if (world.isClientSide) {
			return;
		}

		// Figure out who owns the multiblock, by majority

		Multiset<GameProfile> owners = HashMultiset.create();
		for (IMultiblockComponent part : connectedParts) {
			GameProfile owner = part.getOwner();
			if (owner != null) {
				owners.add(owner);
			}
		}

		GameProfile owner = null;
		int max = 0;
		for (Multiset.Entry<GameProfile> entry : owners.entrySet()) {
			int count = entry.getCount();
			if (count > max) {
				max = count;
				owner = entry.getElement();
			}
		}

		if (owner != null) {
			getOwnerHandler().setOwner(owner);
		}
	}

	/* INbtWritable */
	@Override
	public CompoundTag write(CompoundTag data) {
		ownerHandler.write(data);
		return data;
	}

	@Override
	public void read(CompoundTag data) {
		ownerHandler.read(data);
	}

	/* INVENTORY */

	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public void setChanged() {
		getInternalInventory().setChanged();
	}

	@Override
	public final int getContainerSize() {
		return getInternalInventory().getContainerSize();
	}

	@Override
	public final ItemStack getItem(int slotIndex) {
		return getInternalInventory().getItem(slotIndex);
	}

	@Override
	public final ItemStack removeItem(int slotIndex, int amount) {
		return getInternalInventory().removeItem(slotIndex, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
		return getInternalInventory().removeItemNoUpdate(slotIndex);
	}

	@Override
	public final void setItem(int slotIndex, ItemStack itemstack) {
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

	//TODO inventory title
	//	@Override
	//	public String getName() {
	//		return getInternalInventory().getName();
	//	}
	//
	//	@Override
	//	public ITextComponent getDisplayName() {
	//		return getInternalInventory().getDisplayName();
	//	}

	@Override
	public final boolean stillValid(Player player) {
		return getInternalInventory().stillValid(player);
	}

	//TODO inventory title
	//	@Override
	//	public boolean hasCustomName() {
	//		return getInternalInventory().hasCustomName();
	//	}

	@Override
	public final boolean canPlaceItem(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canPlaceItem(slotIndex, itemStack);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return getInternalInventory().getSlotsForFace(side);
	}

	@Override
	public final boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemStack, Direction side) {
		return getInternalInventory().canPlaceItemThroughFace(slotIndex, itemStack, side);
	}

	@Override
	public final boolean canTakeItemThroughFace(int slotIndex, ItemStack itemStack, Direction side) {
		return getInternalInventory().canTakeItemThroughFace(slotIndex, itemStack, side);
	}

	//TODO inventory field
	//	@Override
	//	public int getField(int id) {
	//		return getInternalInventory().getField(id);
	//	}
	//
	//	@Override
	//	public int getFieldCount() {
	//		return getInternalInventory().getFieldCount();
	//	}
	//
	//	@Override
	//	public void setField(int id, int value) {
	//		getInternalInventory().setField(id, value);
	//	}

	@Override
	public void clearContent() {
		getInternalInventory().clearContent();
	}
}
