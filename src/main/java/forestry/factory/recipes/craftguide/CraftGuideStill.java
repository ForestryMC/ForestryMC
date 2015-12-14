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
package forestry.factory.recipes.craftguide;

import net.minecraft.item.ItemStack;

import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.blocks.BlockFactoryTesrType;
import forestry.plugins.PluginFactory;

import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.LiquidSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

public class CraftGuideStill implements RecipeProvider {

	private final Slot[] slots = new Slot[3];

	public CraftGuideStill() {
		slots[0] = new LiquidSlot(12, 21);
		slots[1] = new LiquidSlot(50, 21).setSlotType(SlotType.OUTPUT_SLOT);
		slots[2] = new ItemSlot(31, 21, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {
		if (PluginFactory.blocks.factoryTESR == null) {
			return;
		}

		ItemStack machine = PluginFactory.blocks.factoryTESR.get(BlockFactoryTesrType.STILL);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
			Object[] array = new Object[3];

			array[0] = recipe.getInput();
			array[1] = recipe.getOutput();
			array[2] = machine;
			generator.addRecipe(template, array);
		}
	}
}
