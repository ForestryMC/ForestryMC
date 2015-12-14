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

import net.minecraftforge.fluids.FluidContainerRegistry;

import forestry.factory.blocks.BlockFactoryTesrType;
import forestry.factory.recipes.BottlerRecipe;
import forestry.plugins.PluginFactory;

import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.LiquidSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

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
		if (PluginFactory.blocks.factoryTESR == null) {
			return;
		}

		ItemStack machine = PluginFactory.blocks.factoryTESR.get(BlockFactoryTesrType.BOTTLER);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

		for (FluidContainerRegistry.FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			BottlerRecipe recipe = BottlerRecipe.getRecipe(container.fluid, container.emptyContainer);
			if (recipe != null) {
				Object[] array = new Object[4];
				
				array[0] = recipe.empty;
				array[1] = recipe.input;
				array[2] = recipe.filled;
				array[3] = machine;
				generator.addRecipe(template, array);
			}
		}
	}
}
