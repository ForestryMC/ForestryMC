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
package forestry.greenhouse.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.ICamouflageHandler;
import forestry.core.inventory.ItemInventory;
import forestry.core.tiles.ITitled;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.camouflage.CamouflageHandlerType;
import forestry.greenhouse.network.packets.PacketCamouflageSelectionServer;

public class ItemInventoryCamouflageSprayCan extends ItemInventory implements ICamouflageHandler, ITitled {

	public ItemInventoryCamouflageSprayCan(EntityPlayer player, ItemStack parent) {
		super(player, 1, parent);
	}

	@Override
	public ItemStack getCamouflageBlock() {
		return getStackInSlot(0);
	}

	@Override
	public ItemStack getDefaultCamouflageBlock() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean setCamouflageBlock(ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, getStackInSlot(0))) {
			setInventorySlotContents(0, camouflageBlock);

			World world = player.world;
			if (sendClientUpdate && world != null && world.isRemote) {
				NetworkUtil.sendToServer(new PacketCamouflageSelectionServer(this, CamouflageHandlerType.ITEM));
			}
			return true;
		}
		return false;
	}

	@Override
	public String getUnlocalizedTitle() {
		return "for.gui.camouflage_spray_can";
	}

	@Override
	public BlockPos getCoordinates() {
		return new BlockPos(player);
	}

	@Override
	public World getWorldObj() {
		return null;
	}
}
