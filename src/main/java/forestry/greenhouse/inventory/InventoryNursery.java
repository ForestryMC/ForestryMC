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

import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.SlotUtil;
import forestry.greenhouse.tiles.TileGreenhouseNursery;
import forestry.lepidopterology.PluginLepidopterology;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InventoryNursery extends InventoryAdapterTile<TileGreenhouseNursery> {

	public static final short SLOT_WORK = 0;
	public static final short SLOT_OUTPUT_1 = 1;
	public static final short SLOT_OUTPUT_COUNT = 6;
	public static final short SLOT_INPUT_1 = 7;
	public static final short SLOT_INPUT_COUNT = 6;
	public static final short SLOTS = 13;

	public InventoryNursery(TileGreenhouseNursery tile) {
		super(tile, SLOTS, "Items", 64);
	}
	
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if(SlotUtil.isSlotInRange(slotIndex, SLOT_INPUT_1, SLOT_INPUT_COUNT)){
			return itemStack.getItem() == PluginLepidopterology.getItems().cocoonGE;
		}
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_OUTPUT_1, SLOT_OUTPUT_COUNT);
	}

}
