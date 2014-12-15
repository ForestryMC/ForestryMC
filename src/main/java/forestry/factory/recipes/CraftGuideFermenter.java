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

import forestry.api.fuels.FuelManager;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.factory.gadgets.MachineFermenter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
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

		if (ForestryBlock.factoryTESR.block() == null)
			return;

		ItemStack machine = ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_FERMENTER_META);
		RecipeTemplate template = generator.createRecipeTemplate(slots, machine);
		List<Object> fuels = new ArrayList<Object>(FuelManager.fermenterFuel.keySet());

		for (MachineFermenter.Recipe recipe : MachineFermenter.RecipeManager.recipes) {
			Object[] array = new Object[5];

			array[0] = recipe.resource;
			array[1] = fuels;
			array[2] = recipe.liquid;
			FluidStack output = recipe.output.copy();
			output.amount *= recipe.fermentationValue;
			output.amount *= recipe.modifier;
			array[3] = output;
			array[4] = machine;
			generator.addRecipe(template, array);
		}
	}
}
