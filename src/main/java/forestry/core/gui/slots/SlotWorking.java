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
package forestry.core.gui.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class SlotWorking extends SlotForestry {

	public SlotWorking(Container iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
		blockShift();
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		return false;
	}

	@Override
	public ItemStack onTake(Player player, ItemStack itemStack) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean mayPickup(Player stack) {
		return false;
	}

	@Override
	public ItemStack remove(int i) {
		return ItemStack.EMPTY;
	}
}
