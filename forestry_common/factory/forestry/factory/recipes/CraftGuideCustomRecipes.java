/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.factory.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.SlotType;

import forestry.core.interfaces.IDescriptiveRecipe;
import forestry.core.utils.RecipeUtil;

public class CraftGuideCustomRecipes implements RecipeProvider {

	private final ItemSlot[] slots = new ItemSlot[10];

	public CraftGuideCustomRecipes() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				slots[i + j * 3] = new ItemSlot(i * 18 + 3, j * 18 + 3, 16, 16);
		slots[9] = new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		ItemStack machine = new ItemStack(Blocks.crafting_table);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine, "/gui/CraftGuideRecipe.png", 1, 1, 82, 1);

		for (Object rec : CraftingManager.getInstance().getRecipeList()) {
			if (!(rec instanceof IDescriptiveRecipe))
				continue;

			Object[] array = new Object[10];

			Object[] flattened = RecipeUtil.getCraftingRecipeAsArray(rec);
			if (flattened == null)
				continue;
			System.arraycopy(flattened, 0, array, 0, flattened.length);
			generator.addRecipe(template, array);

		}
	}

}
