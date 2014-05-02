/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.factory.gadgets.MachineBottler;

public class CraftGuideBottler implements RecipeProvider {

	private final Slot[] slots = new Slot[4];

	public CraftGuideBottler() {
		slots[0] = new ItemSlot(12, 12, 16, 16).drawOwnBackground();
		slots[1] = new LiquidSlot(12, 30);
		slots[2] = new ItemSlot(50, 21, 16, 16).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
		slots[3] = new ItemSlot(31, 21, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		if (ForestryBlock.factoryTESR == null)
			return;

		ItemStack machine = new ItemStack(ForestryBlock.factoryTESR, 1, Defaults.DEFINITION_BOTTLER_META);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (MachineBottler.Recipe recipe : MachineBottler.RecipeManager.recipes) {
			Object[] array = new Object[4];

			array[0] = recipe.can;
			array[1] = recipe.input;
			array[2] = recipe.bottled;
			array[3] = machine;
			generator.addRecipe(template, array);
		}
	}
}
