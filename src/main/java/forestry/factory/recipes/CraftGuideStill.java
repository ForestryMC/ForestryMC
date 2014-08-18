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
import forestry.factory.gadgets.MachineStill;

public class CraftGuideStill implements RecipeProvider {

	private final Slot[] slots = new Slot[3];

	public CraftGuideStill() {
		slots[0] = new LiquidSlot(12, 21);
		slots[1] = new LiquidSlot(50, 21).setSlotType(SlotType.OUTPUT_SLOT);
		slots[2] = new ItemSlot(31, 21, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		if (ForestryBlock.factoryTESR == null)
			return;

		ItemStack machine = ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_STILL_META);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (MachineStill.Recipe recipe : MachineStill.RecipeManager.recipes) {
			Object[] array = new Object[3];

			array[0] = recipe.input;
			array[1] = recipe.output;
			array[2] = machine;
			generator.addRecipe(template, array);
		}
	}
}
