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
package forestry.core.recipes.craftguide;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.RecipeUtil;

import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.SlotType;

public class CraftGuideCustomRecipes implements RecipeProvider {

	private final ItemSlot[] slots = new ItemSlot[10];

	public CraftGuideCustomRecipes() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				slots[i + j * 3] = new ItemSlot(i * 18 + 3, j * 18 + 3, 16, 16);
			}
		}
		slots[9] = new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		ItemStack machine = new ItemStack(Blocks.crafting_table);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine, "/gui/CraftGuideRecipe.png", 1, 1, 82, 1);

		for (Object rec : CraftingManager.getInstance().getRecipeList()) {
			if (!(rec instanceof IDescriptiveRecipe)) {
				continue;
			}

			Object[] array = new Object[10];

			Object[] flattened = RecipeUtil.getCraftingRecipeAsArray((IDescriptiveRecipe) rec);
			if (flattened == null) {
				continue;
			}
			System.arraycopy(flattened, 0, array, 0, flattened.length);
			generator.addRecipe(template, array);

		}
	}

}
