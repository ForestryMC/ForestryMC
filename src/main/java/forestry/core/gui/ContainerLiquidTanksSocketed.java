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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.circuits.ISocketable;
import forestry.core.tiles.ILiquidTankTile;

public abstract class ContainerLiquidTanksSocketed<T extends TileEntity & ILiquidTankTile & ISocketable> extends ContainerTile<T> implements IContainerSocketed, IContainerLiquidTanks {

	private final ContainerSocketedHelper<T> socketedHelper;
	private final ContainerLiquidTanksHelper<T> tanksHelper;

	protected ContainerLiquidTanksSocketed(int windowId, ContainerType<?> type, PlayerInventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type, playerInventory, tile, xInv, yInv);
		this.socketedHelper = new ContainerSocketedHelper<>(this.tile);
		this.tanksHelper = new ContainerLiquidTanksHelper<>(this.tile);
	}

	/* IContainerLiquidTanks */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handlePipetteClickClient(int slot, PlayerEntity player) {
		tanksHelper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, ServerPlayerEntity player) {
		tanksHelper.handlePipetteClick(slot, player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		tile.getTankManager().sendTankUpdate(this, listeners);
	}

	@Override
	public void addListener(IContainerListener crafting) {
		super.addListener(crafting);
		tile.getTankManager().containerAdded(this, crafting);
	}

	@Override
	public void onContainerClosed(PlayerEntity PlayerEntity) {
		super.onContainerClosed(PlayerEntity);
		tile.getTankManager().containerRemoved(this);
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}

	/* IContainerSocketed */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleChipsetClick(int slot) {
		socketedHelper.handleChipsetClick(slot);
	}

	@Override
	public void handleChipsetClickServer(int slot, ServerPlayerEntity player, ItemStack itemstack) {
		socketedHelper.handleChipsetClickServer(slot, player, itemstack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleSolderingIronClick(int slot) {
		socketedHelper.handleSolderingIronClick(slot);
	}

	@Override
	public void handleSolderingIronClickServer(int slot, ServerPlayerEntity player, ItemStack itemstack) {
		socketedHelper.handleSolderingIronClickServer(slot, player, itemstack);
	}

}
