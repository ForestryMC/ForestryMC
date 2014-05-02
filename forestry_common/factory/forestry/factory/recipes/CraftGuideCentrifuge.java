/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.factory.recipes;

import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

import uristqwerty.CraftGuide.api.ChanceSlot;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.factory.gadgets.MachineCentrifuge;

public class CraftGuideCentrifuge implements RecipeProvider {

	private final Slot[] slots = new Slot[11];

	public CraftGuideCentrifuge() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				slots[i + j * 3] = new ChanceSlot(i * 18 + 24, j * 18 + 3, 16, 16).setRatio(100).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
		slots[9] = new ItemSlot(4, 31, 16, 16, true).drawOwnBackground();
		slots[10] = new ItemSlot(4, 11, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		if (ForestryBlock.factoryTESR == null)
			return;

		ItemStack machine = new ItemStack(ForestryBlock.factoryTESR, 1, Defaults.DEFINITION_CENTRIFUGE_META);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (MachineCentrifuge.Recipe recipe : MachineCentrifuge.RecipeManager.recipes) {
			Object[] array = new Object[11];
			@SuppressWarnings("unchecked")
			Entry<ItemStack, Integer>[] entries = recipe.products.entrySet().toArray(new Entry[0]);

			for (int i = 0; i < Math.min(entries.length, 9); i++) {
				array[i] = entries[i].getValue() > 0 ? new Object[] { entries[i].getKey(), entries[i].getValue() } : null;
			}

			array[9] = recipe.resource;
			array[10] = machine;

			generator.addRecipe(template, array);
		}
	}
}
