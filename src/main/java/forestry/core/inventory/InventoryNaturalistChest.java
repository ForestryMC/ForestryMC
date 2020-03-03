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
package forestry.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.tiles.TileNaturalistChest;
import forestry.core.utils.OreDictUtil;

public class InventoryNaturalistChest extends InventoryAdapterTile<TileNaturalistChest> {
	public static final int treeSaplingId = OreDictionary.getOreID(OreDictUtil.TREE_SAPLING);

	private final ISpeciesRoot speciesRoot;

	public InventoryNaturalistChest(TileNaturalistChest tile, ISpeciesRoot speciesRoot) {
		super(tile, 125, "Items");
		this.speciesRoot = speciesRoot;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}
		if (speciesRoot.isMember(itemstack)) {
			return true;
		}
		if (speciesRoot != TreeManager.treeRoot) {
			return false;
		}
		int[] oreIds = OreDictionary.getOreIDs(itemstack);
		for (int oreId : oreIds) {
			if (oreId == treeSaplingId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing side) {
		return true;
	}
}
