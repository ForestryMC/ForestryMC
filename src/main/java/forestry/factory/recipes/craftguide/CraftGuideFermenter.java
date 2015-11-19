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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
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

public class CraftGuideFermenter implements RecipeProvider {

	private final Slot[] slots = new Slot[5];

	public CraftGuideFermenter() {
		slots[0] = new ItemSlot(3, 12, 16, 16, true);
		slots[1] = new ItemSlot(3, 30, 16, 16, true);
		slots[2] = new LiquidSlot(21, 21);
		slots[3] = new LiquidSlot(59, 21).setSlotType(SlotType.OUTPUT_SLOT);
		slots[4] = new ItemSlot(40, 21, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
	}

	@Override
	public void generateRecipes(RecipeGenerator generator) {

		if (PluginFactory.blocks.factoryTESR == null) {
			return;
		}

		ItemStack machine = PluginFactory.blocks.factoryTESR.get(BlockFactoryTesrType.FERMENTER);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);
		List<Object> fuels = new ArrayList<Object>(FuelManager.fermenterFuel.keySet());

		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
			Object[] array = new Object[5];

			array[0] = recipe.getResource();
			array[1] = fuels;
			array[2] = recipe.getFluidResource();
			int amount = Math.round(recipe.getFermentationValue() * recipe.getModifier());
			FluidStack output = new FluidStack(recipe.getOutput(), amount);
			array[3] = output;
			array[4] = machine;
			generator.addRecipe(template, array);
		}
	}
}
