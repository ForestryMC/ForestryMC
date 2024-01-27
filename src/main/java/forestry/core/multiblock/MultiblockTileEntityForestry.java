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

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.network.NetworkHooks;

import forestry.api.core.ILocatable;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.api.multiblock.MultiblockTileEntityBase;
import forestry.core.config.Constants;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.tiles.IFilterSlotDelegate;

public abstract class MultiblockTileEntityForestry<T extends IMultiblockLogic> extends MultiblockTileEntityBase<T> implements WorldlyContainer, IFilterSlotDelegate, ILocatable, MenuProvider {
	@Nullable
	private GameProfile owner;

	public MultiblockTileEntityForestry(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, T multiblockLogic) {
		super(tileEntityType, pos, state, multiblockLogic);
	}

	/**
	 * Called by a structure block when it is right clicked by a player.
	 */
	public void openGui(ServerPlayer player, BlockPos pos) {
		NetworkHooks.openScreen(player, this, pos);
	}

	@Override
	public void load(CompoundTag data) {
		super.load(data);

		if (data.contains("owner")) {
			CompoundTag ownerNbt = data.getCompound("owner");
			this.owner = NbtUtils.readGameProfile(ownerNbt);
		}

		getInternalInventory().read(data);
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);

		if (this.owner != null) {
			CompoundTag nbt = new CompoundTag();
			NbtUtils.writeGameProfile(nbt, owner);
			data.put("owner", nbt);
		}

		getInternalInventory().write(data);
	}

	/* INVENTORY */
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	public boolean allowsAutomation() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return getInternalInventory().isEmpty();
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

	@Override
	public final boolean stillValid(Player player) {
		return getInternalInventory().stillValid(player);
	}

	@Override
	public final boolean canPlaceItem(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canPlaceItem(slotIndex, itemStack);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (allowsAutomation()) {
			return getInternalInventory().getSlotsForFace(side);
		} else {
			return Constants.SLOTS_NONE;
		}
	}

	@Override
	public final boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemStack, Direction side) {
		return allowsAutomation() && getInternalInventory().canPlaceItemThroughFace(slotIndex, itemStack, side);
	}

	@Override
	public final boolean canTakeItemThroughFace(int slotIndex, ItemStack itemStack, Direction side) {
		return allowsAutomation() && getInternalInventory().canTakeItemThroughFace(slotIndex, itemStack, side);
	}

	@Override
	public final boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public final boolean isLocked(int slotIndex) {
		return getInternalInventory().isLocked(slotIndex);
	}

	/* ILocatable */
	@Override
	public final Level getWorldObj() {
		return level;
	}

	/* IMultiblockComponent */

	@Override
	@Nullable
	public final GameProfile getOwner() {
		return owner;
	}

	public final void setOwner(GameProfile owner) {
		this.owner = owner;
	}


	@Override
	public void clearContent() {
		getInternalInventory().clearContent();
	}
}
