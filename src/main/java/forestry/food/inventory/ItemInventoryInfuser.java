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
package forestry.food.inventory;

import forestry.api.food.BeverageManager;
import forestry.core.inventory.ItemInventory;
import forestry.food.PluginFood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class ItemInventoryInfuser extends ItemInventory {
	private static final short inputSlot = 0;
	private static final short outputSlot = 1;
	private static final short ingredientSlot1 = 2;
	private static final short ingredientSlotCount = 4;

	public ItemInventoryInfuser(EntityPlayer player, ItemStack itemStack) {
		super(player, 6, itemStack);
	}

	@Override
	public void onSlotClick(int slotIndex, EntityPlayer player) {

		// Need input
		ItemStack input = getStackInSlot(inputSlot);
		if (!input.isEmpty() &&
				getStackInSlot(outputSlot).isEmpty() &&
				PluginFood.items.beverage == input.getItem()) {

			// Create the seasoned item
			NonNullList<ItemStack> ingredients = NonNullList.create();
			for (int i = 0; i < ingredientSlotCount; i++) {
				ingredients.add(getStackInSlot(i + ingredientSlot1));
			}

			// Only continue if there is anything to season
			if (BeverageManager.infuserManager.hasMixtures(ingredients)) {

				ItemStack seasoned = BeverageManager.infuserManager.getSeasoned(input, ingredients);
				if (!seasoned.isEmpty()) {

					// Remove required ingredients.
					NonNullList<ItemStack> toRemove = BeverageManager.infuserManager.getRequired(ingredients);
					for (ItemStack templ : toRemove) {
						ItemStack ghost = templ.copy();

						for (int i = ingredientSlot1; i < this.getSizeInventory() && !ghost.isEmpty(); i++) {
							ItemStack ingredient = getStackInSlot(i);
							if (!ingredient.isEmpty() && OreDictionary.itemMatches(ghost, ingredient, false)) {
								ItemStack removed = decrStackSize(i, 1);
								ghost.shrink(removed.getCount());
							}
						}
					}
					decrStackSize(inputSlot, 1);
					setInventorySlotContents(outputSlot, seasoned);
				}
			}
		}
	}


	@Override
	public String getName() {
		return "Infuser";
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == inputSlot) {
			return PluginFood.items.beverage == itemStack.getItem();
		} else if (slotIndex >= ingredientSlot1 && slotIndex < ingredientSlot1 + ingredientSlotCount) {
			return BeverageManager.infuserManager.isIngredient(itemStack);
		}
		return false;
	}
}
