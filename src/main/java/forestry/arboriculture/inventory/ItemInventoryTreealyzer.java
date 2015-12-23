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
package forestry.arboriculture.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.TreeManager;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.GeneticsUtil;

public class ItemInventoryTreealyzer extends ItemInventoryAlyzer implements IHintSource {

	public ItemInventoryTreealyzer(EntityPlayer player, ItemStack itemStack) {
		super(TreeManager.treeRoot, player, itemStack);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (!TreeManager.treeRoot.isMember(itemStack)) {
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(itemStack);
			if (ersatz != null) {
				return super.canSlotAccept(slotIndex, ersatz);
			}
		}
		return super.canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public void onSlotClick(EntityPlayer player) {
		ItemStack specimen = getStackInSlot(SLOT_SPECIMEN);
		if (!TreeManager.treeRoot.isMember(specimen)) {
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(specimen);
			if (ersatz != null) {
				setInventorySlotContents(SLOT_SPECIMEN, ersatz);
			}
		}

		super.onSlotClick(player);
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("treealyzer");
	}
}
