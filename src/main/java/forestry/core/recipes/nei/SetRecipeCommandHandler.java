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
package forestry.core.recipes.nei;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author bdew
 */
public class SetRecipeCommandHandler {
	private final Class<? extends Container> containerClass;
	private final Class<? extends Slot> slotClass;

	public SetRecipeCommandHandler(Class<? extends Container> containerClass, Class<? extends Slot> slotClass) {
		this.containerClass = containerClass;
		this.slotClass = slotClass;
	}

	public void handle(NBTTagCompound data, EntityPlayerMP player) {
		NBTTagList stacks = data.getTagList("stacks", 10);
		Container cont = player.openContainer;
		if (!containerClass.isInstance(cont)) {
			return;
		}

		Map<Integer, ItemStack> stmap = new HashMap<>();
		for (int i = 0; i < stacks.tagCount(); i++) {
			NBTTagCompound itemdata = stacks.getCompoundTagAt(i);
			stmap.put(itemdata.getInteger("slot"), ItemStack.loadItemStackFromNBT(itemdata));
		}
		for (Object slotobj : cont.inventorySlots) {
			if (!slotClass.isInstance(slotobj)) {
				continue;
			}

			Slot slot = (Slot) slotobj;
			if (stmap.containsKey(slot.getSlotIndex())) {
				slot.putStack(stmap.get(slot.getSlotIndex()));
			} else {
				slot.putStack(null);
			}
		}
	}
}
