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

import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.blocks.BlockFactoryTesrType;
import forestry.plugins.PluginFactory;

import uristqwerty.CraftGuide.api.ChanceSlot;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.LiquidSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

public class CraftGuideSqueezer implements RecipeProvider {

	private final Slot[] slots = new Slot[12];

	public CraftGuideSqueezer() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				slots[i + j * 3] = new ItemSlot(i * 18 + 3, j * 18 + 3, 16, 16).drawOwnBackground();
			}
		}
		slots[9] = new ChanceSlot(59, 39, 16, 16, true).setRatio(100).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
		slots[10] = new LiquidSlot(59, 21).setSlotType(SlotType.OUTPUT_SLOT);
		slots[11] = new ItemSlot(59, 3, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		if (PluginFactory.blocks.factoryTESR == null) {
			return;
		}

		ItemStack machine = PluginFactory.blocks.factoryTESR.get(BlockFactoryTesrType.SQUEEZER);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
			Object[] array = new Object[12];

			System.arraycopy(recipe.getResources(), 0, array, 0, recipe.getResources().length);
			if (recipe.getRemnants() != null) {
				array[9] = recipe.getRemnantsChance() > 0 ? new Object[]{recipe.getRemnants().copy(), recipe.getRemnantsChance()} : null;
			}
			if (recipe.getFluidOutput() != null) {
				array[10] = recipe.getFluidOutput();
			}
			array[11] = machine;
			generator.addRecipe(template, array);
		}
	}

}
