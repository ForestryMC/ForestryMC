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
package forestry.farming.multiblock;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

import forestry.api.farming.IFarmable;

/**
 * Inventory of the farm multiblock.
 */
public class InventoryFarm extends InventoryPlantation<FarmController> implements IFarmInventoryInternal {
	public static InventoryPlantation.InventoryConfig CONFIG = new InventoryPlantation.InventoryConfig(
			0, 6,
			6, 6,
			12, 8,
			20, 1,
			21, 1
	);

	public InventoryFarm(FarmController farmController) {
		super(farmController, CONFIG);
	}

	@Override
	public boolean plantGermling(IFarmable germling, Player player, BlockPos pos) {
		for (int i = 0; i < germlingsInventory.getContainerSize(); i++) {
			ItemStack germlingStack = germlingsInventory.getItem(i);
			if (germlingStack.isEmpty() || !germling.isGermling(germlingStack)) {
				continue;
			}

			if (germling.plantSaplingAt(player, germlingStack, player.level, pos)) {
				germlingsInventory.removeItem(i, 1);
				return true;
			}
		}
		return false;
	}

}