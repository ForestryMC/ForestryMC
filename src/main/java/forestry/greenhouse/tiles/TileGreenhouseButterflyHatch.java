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
package forestry.greenhouse.tiles;

import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.utils.InventoryUtil;
import forestry.greenhouse.gui.ContainerGreenhouseButterflyHatch;
import forestry.greenhouse.gui.GuiGreenhouseButterflyHatch;
import forestry.greenhouse.inventory.InventoryGreenhouseButterflyHatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TileGreenhouseButterflyHatch extends TileGreenhouse implements IGreenhouseComponent.ButterflyHatch {

	private final InventoryGreenhouseButterflyHatch inventory;

	public TileGreenhouseButterflyHatch() {
		this.inventory = new InventoryGreenhouseButterflyHatch(this);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public NonNullList<ItemStack> addCocoonLoot(IButterflyCocoon cocoon) {
		NonNullList<ItemStack> loots = NonNullList.create();
		for (ItemStack loot : cocoon.getCaterpillar().getCocoonDrop(cocoon)) {
			if (!InventoryUtil.tryAddStack(this, loot, 0, InventoryGreenhouseButterflyHatch.SLOTS, true)) {
				loots.add(loot);
			}
		}
		return loots;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiGreenhouseButterflyHatch(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerGreenhouseButterflyHatch(player.inventory, this);
	}

}
