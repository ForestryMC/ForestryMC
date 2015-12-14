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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.circuits.ISocketable;

public abstract class ContainerSocketed<T extends TileEntity & ISocketable> extends ContainerTile<T> implements IContainerSocketed {

	private final ContainerSocketedHelper<T> helper;

	protected ContainerSocketed(T tile, InventoryPlayer playerInventory, int xInv, int yInv) {
		super(tile, playerInventory, xInv, yInv);
		this.helper = new ContainerSocketedHelper<>(tile);
	}

	@Override
	public void handleChipsetClick(int slot) {
		helper.handleChipsetClick(slot);
	}

	@Override
	public void handleChipsetClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		helper.handleChipsetClickServer(slot, player, itemstack);
	}

	@Override
	public void handleSolderingIronClick(int slot) {
		helper.handleSolderingIronClick(slot);
	}

	@Override
	public void handleSolderingIronClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		helper.handleSolderingIronClickServer(slot, player, itemstack);
	}
}
