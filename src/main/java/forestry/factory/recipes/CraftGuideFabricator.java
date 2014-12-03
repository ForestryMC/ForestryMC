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
package forestry.factory.recipes;

import net.minecraft.item.ItemStack;

import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.LiquidSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.utils.RecipeUtil;
import forestry.factory.gadgets.MachineFabricator;

public class CraftGuideFabricator implements RecipeProvider {

	private final Slot[] slots = new Slot[12];

	public CraftGuideFabricator() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				slots[i + j * 3] = new ItemSlot(i * 18 + 3, j * 18 + 3, 16, 16).drawOwnBackground();
		slots[9] = new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
		slots[10] = new LiquidSlot(59, 39);
		slots[11] = new ItemSlot(59, 3, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		if (ForestryBlock.factoryTESR.block() == null)
			return;

		ItemStack machine = ForestryBlock.factoryPlain.getItemStack(1, Defaults.DEFINITION_FABRICATOR_META);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (MachineFabricator.Recipe recipe : MachineFabricator.RecipeManager.recipes) {
			Object[] array = new Object[12];

			Object[] flattened = RecipeUtil.getCraftingRecipeAsArray(recipe.asIRecipe());
			if (flattened == null)
				flattened = generator.getCraftingRecipe(recipe.asIRecipe());
			if (flattened == null)
				continue;
			System.arraycopy(flattened, 0, array, 0, flattened.length);
			if (recipe.getLiquid() != null)
				array[10] = recipe.getLiquid();
			array[11] = machine;
			generator.addRecipe(template, array);
		}
	}

}
