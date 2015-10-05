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
package forestry.apiculture.gadgets;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.network.GuiId;
import forestry.core.utils.PlainInventory;
import forestry.core.utils.Utils;

public class TileApiaristChest extends TileNaturalistChest {

	private boolean checkedForLegacyBlock = false;

	public TileApiaristChest() {
		super(BeeManager.beeRoot, GuiId.ApiaristChestGUI.ordinal());
	}

	@Override
	protected void updateServerSide() {
		if (worldObj != null && !checkedForLegacyBlock) {
			Block block = worldObj.getBlockState(pos).getBlock();
			if (ForestryBlock.apiculture.isBlockEqual(block)) {
				migrateFromLegacyBlock();
			}

			checkedForLegacyBlock = true;
		}
	}

	private void migrateFromLegacyBlock() {
		IInventory inventoryCopy = new PlainInventory(getInternalInventory());

		// clear the inventory so it isn't dropped when the block is replaced
		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, null);
		}

		worldObj.setBlockToAir(pos);
		worldObj.setBlockState(pos, ForestryBlock.apicultureChest.block().getStateFromMeta(Defaults.DEFINITION_APIARISTCHEST_META), Defaults.FLAG_BLOCK_SYNCH_AND_UPDATE);

		TileApiaristChest tile = Utils.getTile(worldObj, pos, TileApiaristChest.class);
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = inventoryCopy.getStackInSlot(i);
			tile.setInventorySlotContents(i, stack);
		}
		tile.markDirty();
	}

}