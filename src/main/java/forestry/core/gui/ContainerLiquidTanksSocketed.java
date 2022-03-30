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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.circuits.ISocketable;
import forestry.core.tiles.ILiquidTankTile;

public abstract class ContainerLiquidTanksSocketed<T extends BlockEntity & ILiquidTankTile & ISocketable> extends ContainerTile<T> implements IContainerSocketed, IContainerLiquidTanks {

	private final ContainerSocketedHelper<T> socketedHelper;
	private final ContainerLiquidTanksHelper<T> tanksHelper;

	protected ContainerLiquidTanksSocketed(int windowId, MenuType<?> type, Inventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type, playerInventory, tile, xInv, yInv);
		this.socketedHelper = new ContainerSocketedHelper<>(this.tile);
		this.tanksHelper = new ContainerLiquidTanksHelper<>(this.tile);
	}

	/* IContainerLiquidTanks */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handlePipetteClickClient(int slot, Player player) {
		tanksHelper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, ServerPlayer player) {
		tanksHelper.handlePipetteClick(slot, player);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		tile.getTankManager().sendTankUpdate(this, containerListeners);
	}

	@Override
	public void addSlotListener(ContainerListener crafting) {
		super.addSlotListener(crafting);
		tile.getTankManager().containerAdded(this, crafting);
	}

	@Override
	public void removed(Player PlayerEntity) {
		super.removed(PlayerEntity);
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
	public void handleChipsetClickServer(int slot, ServerPlayer player, ItemStack itemstack) {
		socketedHelper.handleChipsetClickServer(slot, player, itemstack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleSolderingIronClick(int slot) {
		socketedHelper.handleSolderingIronClick(slot);
	}

	@Override
	public void handleSolderingIronClickServer(int slot, ServerPlayer player, ItemStack itemstack) {
		socketedHelper.handleSolderingIronClickServer(slot, player, itemstack);
	}

}
